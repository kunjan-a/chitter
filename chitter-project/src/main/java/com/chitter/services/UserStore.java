package com.chitter.services;

import com.chitter.model.UserItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
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
    public SimpleJdbcTemplate db;

    @Autowired
    public UserStore(@Qualifier("userID") ThreadLocal<Long> userID, SimpleJdbcTemplate template) {
        this.userID = userID;
        db = template;
    }


    public UserItem add(UserItem userItem) {
        try {
            long id = db.queryForLong("insert into users (name, email, password, photo_path) values(?, ?, ?, ?) returning id", userItem.getName(), userItem.getEmail(), userItem.getPassword(), userItem.getPhotoPath());
//            long id = db.queryForLong("select currval('users_id_seq');");
            return getUserWithId(id);
        } catch (DataAccessException e) {
            return null;
        }
    }


    private UserItem getUserWithId(long id) {
        try {
            return db.queryForObject("select * from users where id = ?", UserItem.rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    public UserItem getUserWithCredentials(UserItem userItem) {
        try {
            return db.queryForObject("select * from users where email = ? AND password = ?", UserItem.rowMapper, userItem.getEmail(), userItem.getPassword());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    public UserItem getUserWithEmail(UserItem userItem) {
        try {
            return db.queryForObject("select * from users where email = ?", UserItem.rowMapper, userItem.getEmail());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
