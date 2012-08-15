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
public class FavouriteUserItem {

    private long user_id, favourite_user_id;

    public FavouriteUserItem(long follower_id, long favourite_user_id) {
        this.user_id = follower_id;
        this.favourite_user_id = favourite_user_id;
    }

    public long getFavourite_user_id() {
        return favourite_user_id;
    }

    public void setFavourite_user_id(long favourite_user_id) {
        this.favourite_user_id = favourite_user_id;
    }

    public long getUser_id() {
        return user_id;

    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public static final RowMapper<FavouriteUserItem> rowMapper = new RowMapper<FavouriteUserItem>() {
        @Override
        public FavouriteUserItem mapRow(ResultSet resultSet, int i) throws SQLException {
            return new FavouriteUserItem(resultSet);
        }
    };

    public FavouriteUserItem(ResultSet resultSet) throws SQLException {
        this.user_id = resultSet.getLong("user_id");
        this.favourite_user_id = resultSet.getLong("favourite_user_id");
    }

    public FavouriteUserItem() {
    }

}