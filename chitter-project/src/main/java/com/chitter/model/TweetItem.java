package com.chitter.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 27/7/12
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class TweetItem {
    private long id;
    private String time;
    private String text = "";
    private long user_id;
    private int retweets = 0;


    public static final RowMapper<TweetItem> rowMapper = new RowMapper<TweetItem>() {
        @Override
        public TweetItem mapRow(ResultSet resultSet, int i) throws SQLException {
            return new TweetItem(resultSet);
        }
    };

    public TweetItem(ResultSet rs) throws SQLException {
        id = rs.getLong("id");
        user_id = rs.getLong("user_id");
        text = rs.getString("text");
        retweets = rs.getInt("retweets");
        time = rs.getString("time");
    }

    public TweetItem() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public int getRetweets() {
        return retweets;
    }

    public void setRetweets(int retweets) {
        this.retweets = retweets;
    }
}