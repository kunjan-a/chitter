package com.chitter.services;

import com.chitter.model.UserItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

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
    private final String PASS = "password";
    private final String PHOTO_PATH = "photo_path";
    private final String USER_ID = "user_id";

    @Autowired
    public UserStore(@Qualifier("userID") ThreadLocal<Long> userID, @Qualifier("namedParameterJdbcTemplate") NamedParameterJdbcTemplate template) {
        this.userID = userID;
        db = template;
    }


    public UserItem add(UserItem userItem) {
        try {

            String sql = "insert into users (name, email, password, photo_path) values(:" + NAME + ", :" + EMAIL + ", :" + PASS + ", :" + PHOTO_PATH + ") ";
            MapSqlParameterSource namedParameters = new MapSqlParameterSource(NAME, userItem.getName());
            namedParameters.addValue(EMAIL, userItem.getEmail());
            namedParameters.addValue(PASS, userItem.getPassword());
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

    public UserItem getUserWithCredentials(UserItem userItem) {
        try {
            String sql = "select * from users where email = :" + EMAIL + " AND password = :" + PASS;
            MapSqlParameterSource namedParameters = new MapSqlParameterSource(EMAIL, userItem.getEmail());
            namedParameters.addValue(PASS, userItem.getPassword());
            return db.queryForObject(sql, namedParameters, UserItem.rowMapper);
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
}
