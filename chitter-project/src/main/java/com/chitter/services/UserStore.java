package com.chitter.services;

import com.chitter.mail.PasswordRecoveryMail;
import com.chitter.model.FeedItem;
import com.chitter.model.UserItem;
import com.chitter.model.UserSearchResultItem;
import com.chitter.security.password.PasswordFactory;
import com.chitter.utils.FileUtil;
import com.chitter.utils.MD5Encoder;
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

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

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
    private final FileUtil fileUtil = new FileUtil();
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
    private final long HOUR_IN_MILLISEC = 60 * 60 * 1000;
    private static final Integer DEF_LIMIT = 10;
    private static final String USER_PATTERN = "userPattern";
    private static final String LIMIT = "limit";
    private static final String BIO = "bio";
    private static final String LOCATION = "location";
    private static final String WEBSITE = "website";

    @Autowired
    public UserStore(@Qualifier("userID") ThreadLocal<Long> userID, @Qualifier("namedParameterJdbcTemplate") NamedParameterJdbcTemplate template) {
        this.userID = userID;
        db = template;
    }


    public UserItem add(UserItem userItem, String password) throws IOException, NoSuchAlgorithmException {
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
            final UserItem userWithEmail = getUserWithEmail(userItem);
            if(userWithEmail!=null){
                final String recoveryToken = getRecoveryToken(userWithEmail);
            }
            return userWithEmail;
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

    public boolean sendRecoveryInfo(UserItem userItem) throws NoSuchAlgorithmException, UnsupportedEncodingException, MessagingException {

        String token = getRecoveryToken(userItem);

        sendRecoveryMail(token,userItem);

        return true;
    }

    private String getRecoveryToken(UserItem userItem) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String token = (new MD5Encoder()).toMd5(userItem.getEmail() + "_" + String.valueOf(System.currentTimeMillis()));

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
        return token;
    }

    private void sendRecoveryMail(String token, UserItem userItem) throws MessagingException {
        new PasswordRecoveryMail(userItem, "http://chitter.com/accountRecovery/" + token).send();
    }

    public UserItem validateAndExpireToken(String recoveryToken) {
        Map<String, Object> values;
        try {
            values = db.queryForMap("select * from recovery where token=:" + TOKEN, new MapSqlParameterSource(TOKEN, recoveryToken));
        } catch (DataAccessException e) {
            return null;
        }

        Timestamp creationTime = (Timestamp) values.get("time");

        db.update("delete from recovery where token=:" + TOKEN, new MapSqlParameterSource(TOKEN, recoveryToken));
        if (System.currentTimeMillis() - creationTime.getTime() < HOUR_IN_MILLISEC)
            return db.queryForObject("select * from users where id=:" + USER_ID, new MapSqlParameterSource(USER_ID, values.get("user_id")), UserItem.rowMapper);
        return null;
    }

    public void storeProfilePic(UserItem useritem, MultipartFile picFile) throws IOException {
        File userPicFile = new File("/var/www/" + useritem.getPhotoPath());
        fileUtil.copyFile(picFile, userPicFile);
    }

    private void storeDefaultProfilePic(long id) throws IOException {
        String photoPath = UserItem.PROFILE_PIC_PATH + String.valueOf(id);
        String sql = "update users set photo_path=:"+PHOTO_PATH+" where id=:"+USER_ID;
        db.update(sql,new MapSqlParameterSource(PHOTO_PATH,photoPath).addValue(USER_ID,id));
        File defaultPic = new File(UserItem.DEFAULT_PROFILE_PIC);
        File userPic = new File("/var/www/" + photoPath);
        fileUtil.copyFile(defaultPic, userPic);

    }

    public List<UserSearchResultItem> getMatchingUsers(String term, Integer numResults) {
        if (numResults == null)
            numResults = DEF_LIMIT;
        String searchTerm = "%" + term + "%";
        ArrayList<HashMap<String, String>> z = new ArrayList<HashMap<String, String>>();

        String sql = "select name,id from users where name ilike :" + USER_PATTERN + " limit :" + LIMIT;
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_PATTERN, searchTerm);
        namedParameters.addValue(LIMIT, numResults);
        final List<UserSearchResultItem> searchResult = db.query(sql, namedParameters, UserSearchResultItem.rowMapper);
        return searchResult;
    }

    public UserItem updateProfileForCurrUser(UserItem userItem) {
        final Long currUserId = userID.get();
        return updateProfileForUserId(currUserId, userItem);
    }

    private UserItem updateProfileForUserId(Long userId, UserItem userItem) {
        String updateName="";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        if(userItem.getName()!=null && !userItem.getName().isEmpty()){
            updateName=", name=:"+NAME;
            namedParameters.addValue(NAME,userItem.getName());
        }
        String sql = "update users set bio=:"+BIO+",location=:"+LOCATION+",website=:"+WEBSITE+updateName+" where id=:"+USER_ID;
        namedParameters.addValue(BIO,userItem.getBio()).addValue(LOCATION,userItem.getLocation()).
                        addValue(WEBSITE, userItem.getWebsite()).addValue(USER_ID, userId);
        db.update(sql,namedParameters);
        return getUserWithId(userId);
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


}
