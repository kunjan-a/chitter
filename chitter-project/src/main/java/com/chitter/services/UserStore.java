package com.chitter.services;

import com.chitter.model.FeedItem;
import com.chitter.model.UserItem;
import com.chitter.security.PasswordFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 27/7/12
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class UserStore {
    private final ThreadLocal<Long> userID;
    public NamedParameterJdbcTemplate db;
    private final String NAME = "name";
    private final String EMAIL = "email";
    private final String SCHEME = "scheme";
    private final String PASS = "password";
    private final String PHOTO_PATH = "photo_path";
    private final String USER_ID = "user_id";
    private final String USER_IDS = "user_ids";
    private final PasswordFactory passwdFactory = new PasswordFactory();
    private final String TOKEN = "token";
    private final String TIME = "time";
    private final long DAY_IN_MILLISEC = 24 * 60 * 60 * 1000;

    @Autowired
    public UserStore(@Qualifier("userID") ThreadLocal<Long> userID, @Qualifier("namedParameterJdbcTemplate") NamedParameterJdbcTemplate template) {
        this.userID = userID;
        db = template;
    }


    public UserItem add(UserItem userItem, String password) throws IOException {
        try {
            password = passwdFactory.getPasswordManager(passwdFactory.CURRENT_SCHEME).getEncodedPasswd(password, userItem.getEmail());
            String sql = "insert into users (name, email, scheme, password) values(:" + NAME + ", :" + EMAIL + ", :" + SCHEME + ", :" + PASS + ") ";
            MapSqlParameterSource namedParameters = new MapSqlParameterSource(NAME, userItem.getName());
            namedParameters.addValue(EMAIL, userItem.getEmail());
            namedParameters.addValue(SCHEME, passwdFactory.CURRENT_SCHEME);
            namedParameters.addValue(PASS, password);

            long id = db.queryForLong(sql + "returning id", namedParameters);
            storeDefaultProfilePic(id);

            return getUserWithId(id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public UserItem getUserWithId(long id) {
        try {
            String sql = "select * from users where id = :" + USER_ID;
            return db.queryForObject(sql, new MapSqlParameterSource(USER_ID, id), UserItem.rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    public UserItem getUserWithCredentials(UserItem userItem, String password) {
        try {
            String sql = "select * from users where email = :" + EMAIL;
            UserCredential userCredential = db.queryForObject(sql, new MapSqlParameterSource(EMAIL, userItem.getEmail()), new RowMapper<UserCredential>() {
                @Override
                public UserCredential mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new UserCredential(rs.getInt("scheme"), rs.getString("password"), UserItem.rowMapper.mapRow(rs, rowNum));
                }
            });

            if (passwdFactory.getPasswordManager(userCredential.scheme).verifyPassword(userCredential.password, password, userItem.getEmail()))
                return userCredential.userItem;

            return null;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    public UserItem getUserWithEmail(UserItem userItem) {
        try {
            String sql = "select * from users where email = :" + EMAIL;
            return db.queryForObject(sql, new MapSqlParameterSource(EMAIL, userItem.getEmail()), UserItem.rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean userExists(long id) {
        String sql = "select count(*) from users where id=:" + USER_ID;
        return (db.queryForInt(sql, new MapSqlParameterSource(USER_ID, id)) == 1);
    }

    public List<UserItem> getUserItems(List<FeedItem> feeds) {
        HashSet<Long> userIds = new HashSet<Long>(feeds.size());
        for (FeedItem feed : feeds) {
            userIds.add(feed.getFeedUserId());
            userIds.add(feed.getTweetUserId());
        }
        return getUsersWithIds(userIds);
    }


    public List<UserItem> getUsersWithIds(HashSet<Long> userIds) {
        if (userIds == null || userIds.isEmpty())
            return new ArrayList<UserItem>(0);
        String sql = "select * from users where id in (:" + USER_IDS + ")";
        return db.query(sql, new MapSqlParameterSource(USER_IDS, userIds), UserItem.rowMapper);
    }

    public int updatePassword(String password, UserItem userItem) {
        String sql = "update users set password =:" + PASS + ", scheme =:" + SCHEME + " where id=:" + USER_ID;
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(SCHEME, passwdFactory.CURRENT_SCHEME);
        namedParameters.addValue(USER_ID, userItem.getId());
        namedParameters.addValue(PASS, passwdFactory.getPasswordManager(passwdFactory.CURRENT_SCHEME).getEncodedPasswd(password, userItem.getEmail()));
        return db.update(sql, namedParameters);
    }

    public boolean sendRecoveryInfo(UserItem userItem) {

        String token = null;
        try {
            token = new BASE64Encoder().encode(MessageDigest.getInstance("MD5").digest(String.valueOf(System.currentTimeMillis()).getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            return false;
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        String sql = "insert into recovery (user_id, token) values (:" + USER_ID + ",:" + TOKEN + ")";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, userItem.getId());
        namedParameters.addValue(TOKEN, token);
        try {
            db.update(sql, namedParameters);
        } catch (DuplicateKeyException duplicateException) {
            sql = "update recovery set token=:" + TOKEN + ",time=:" + TIME + " where user_id=:" + USER_ID;
            namedParameters.addValue(TIME, new Timestamp(System.currentTimeMillis()));
            db.update(sql, namedParameters);
        }
        System.out.println("Verification url is localhost:8080/accountRecovery/" + token);

        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    public UserItem validateAndExpireToken(String recoveryToken) {
        Map<String, Object> values;
        try {
            values = db.queryForMap("select * from recovery where token=:" + TOKEN, new MapSqlParameterSource(TOKEN, recoveryToken));
        } catch (DataAccessException e) {
            return null;
        }

        Timestamp creationTime = (Timestamp) values.get("time");
        if (System.currentTimeMillis() - creationTime.getTime() < DAY_IN_MILLISEC) {

        }
        db.update("delete from recovery where token=:" + TOKEN, new MapSqlParameterSource(TOKEN, recoveryToken));
        UserItem userItem = db.queryForObject("select * from users where id=:" + USER_ID, new MapSqlParameterSource(USER_ID, values.get("user_id")), UserItem.rowMapper);
        return userItem;

    }

    public void storeProfilePic(UserItem useritem, MultipartFile picFile) throws IOException {
        File userPicFile = new File("/var/www/" + useritem.getPhotoPath());
        copyFile(picFile, userPicFile);
    }

    private void storeDefaultProfilePic(long id) throws IOException {
        File defaultPic = new File(UserItem.DEFAULT_PROFILE_PIC);
        File userPic = new File("/var/www/" + UserItem.PROFILE_PIC_PATH + String.valueOf(id) + ".jpg");
        copyFile(defaultPic, userPic);
    }

    private class UserCredential {
        private String password;
        private int scheme;
        private UserItem userItem;

        public UserCredential(int scheme, String password, UserItem userItem) {
            this.scheme = scheme;
            this.password = password;
            this.userItem = userItem;
        }
    }


    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    private void copyFile(MultipartFile source, File dest) throws IOException {
        if (!dest.exists()) {
            dest.createNewFile();
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = source.getInputStream();
            out = new FileOutputStream(dest);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }


}
