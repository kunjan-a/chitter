package com.chitter.model;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 30/7/12
 * Time: 5:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserTweetItem {

    private Long event_id;
    private String time;
    private Long id;
    private String event_type;
    private Long user_id;

    public void setEvent_id(Long event_id) {
        this.event_id = event_id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvent_type() {
        return event_type;

    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public static final String NEW_TWEET = "new";
    public static final String RE_TWEET = "retweet";
    public static final String REPLY_TWEET = "reply";

    public Long getEvent_id() {
        return event_id;
    }

    public String getTime() {
        return time;
    }

    public Long getId() {
        return id;
    }
}
