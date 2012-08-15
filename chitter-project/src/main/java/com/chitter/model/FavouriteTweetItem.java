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
public class FavouriteTweetItem {

    private long user_id, tweet_id;

    public FavouriteTweetItem(long user_id, long tweet_id) {
        this.user_id = user_id;
        this.tweet_id = tweet_id;
    }

    public long getTweet_id() {
        return tweet_id;
    }

    public void setTweet_id(long tweet_id) {
        this.tweet_id = tweet_id;
    }

    public long getUser_id() {
        return user_id;

    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public static final RowMapper<FavouriteTweetItem> rowMapper = new RowMapper<FavouriteTweetItem>() {
        @Override
        public FavouriteTweetItem mapRow(ResultSet resultSet, int i) throws SQLException {
            return new FavouriteTweetItem(resultSet);
        }
    };

    public FavouriteTweetItem(ResultSet resultSet) throws SQLException {
        this.user_id = resultSet.getLong("user_id");
        this.tweet_id = resultSet.getLong("tweet_id");
    }

    public FavouriteTweetItem() {
    }

}