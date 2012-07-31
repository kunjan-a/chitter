package com.chitter.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 27/7/12
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserItem {

    private long id;
    private String name;
    private String email;
    private String password;
    private String photoPath;

    public static final RowMapper<UserItem> rowMapper = new RowMapper<UserItem>() {
        @Override
        public UserItem mapRow(ResultSet resultSet, int i) throws SQLException {
            return new UserItem(resultSet);
        }
    };

    public UserItem(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getLong("id");
        this.name = resultSet.getString("name");
        this.email = resultSet.getString("email");
        this.password = resultSet.getString("password");
        this.photoPath = resultSet.getString("photo_path");
    }

    public UserItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}