package com.chitter.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 30/7/12
 * Time: 5:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserTweetItem {

    private long event_id;
    private Timestamp time;
    private long id;
    private String event_type;
    private long user_id;

    public static final RowMapper<UserTweetItem> rowMapper = new RowMapper<UserTweetItem>() {
        @Override
        public UserTweetItem mapRow(ResultSet resultSet, int i) throws SQLException {
            return new UserTweetItem(resultSet);
        }
    };

    public UserTweetItem() {
    }

    public UserTweetItem(ResultSet rs) throws SQLException {
        id = rs.getLong("id");
        user_id = rs.getLong("user_id");
        event_id = rs.getLong("event_id");
        event_type = rs.getString("event_type");
        time = rs.getTimestamp("time");
    }

    public void setEvent_id(long event_id) {
        this.event_id = event_id;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getEvent_id() {
        return event_id;
    }

    public Timestamp getTime() {
        return time;
    }

    public long getId() {
        return id;
    }


    public static enum TweetEventType {
        NEW_TWEET, RE_TWEET, REPLY_TWEET;

        @Override
        public String toString() {
            switch (this) {
                case NEW_TWEET:
                    return "new";
                case RE_TWEET:
                    return "retweet";
                case REPLY_TWEET:
                    return "reply";
            }
            return super.toString();
        }
    }

}
