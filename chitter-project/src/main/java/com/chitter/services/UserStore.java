package com.chitter.services;

import com.chitter.model.FeedItem;
import com.chitter.model.UserItem;
import com.chitter.security.PasswordFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    @Autowired
    public UserStore(@Qualifier("userID") ThreadLocal<Long> userID, @Qualifier("namedParameterJdbcTemplate") NamedParameterJdbcTemplate template) {
        this.userID = userID;
        db = template;
    }


    public UserItem add(UserItem userItem, String password) {
        try {
            password = passwdFactory.getPasswordManager(passwdFactory.CURRENT_SCHEME).getEncodedPasswd(password, userItem.getEmail());
            String sql = "insert into users (name, email, scheme, password, photo_path) values(:" + NAME + ", :" + EMAIL + ", :" + SCHEME + ", :" + PASS + ", :" + PHOTO_PATH + ") ";
            MapSqlParameterSource namedParameters = new MapSqlParameterSource(NAME, userItem.getName());
            namedParameters.addValue(EMAIL, userItem.getEmail());
            namedParameters.addValue(SCHEME, passwdFactory.CURRENT_SCHEME);
            namedParameters.addValue(PASS, password);
            namedParameters.addValue(PHOTO_PATH, userItem.getPhotoPath());

            long id = db.queryForLong(sql + "returning id", namedParameters);

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
