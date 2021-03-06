package com.chitter.model;

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 27/7/12
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class FeedItem {
    private TweetItem tweetItem;
    private UserTweetItem userTweetItem;

    public FeedItem() {
        tweetItem = new TweetItem();
        userTweetItem = new UserTweetItem();
    }

    public FeedItem(TweetItem tweetItem, UserTweetItem userTweetItem) {
        this.tweetItem = tweetItem;
        this.userTweetItem = userTweetItem;
    }

    public long getTweetId() {
        return tweetItem.getId();
    }

    public void setTweetId(long id) {
        tweetItem.setId(id);
        userTweetItem.setEvent_id(id);
    }

    public Timestamp getTweetTime() {
        return tweetItem.getTime();
    }

    public void setTweetTime(Timestamp time) {
        tweetItem.setTime(time);
    }

    public String getTweetText() {
        return tweetItem.getText();
    }

    public void setTweetText(String text) {
        tweetItem.setText(text);
    }

    public long getTweetUserId() {
        return tweetItem.getUser_id();
    }

    public long getFavourites() {
        return tweetItem.getFavourites();
    }

    public void setTweetUserId(long user_id) {
        tweetItem.setUser_id(user_id);
    }

    public int getRetweets() {
        return tweetItem.getRetweets();
    }

    public void setRetweets(int retweets) {
        tweetItem.setRetweets(retweets);
    }


    public void setFeedTime(Timestamp time) {
        userTweetItem.setTime(time);
    }

    public void setFeedId(Long id) {
        userTweetItem.setId(id);
    }

    public String getFeedType() {
        return userTweetItem.getEvent_type();
    }

    public void setFeedType(String event_type) {
        userTweetItem.setEvent_type(event_type);
    }

    public Long getFeedUserId() {
        return userTweetItem.getUser_id();
    }

    public void setFeedUserId(Long user_id) {
        userTweetItem.setUser_id(user_id);
    }

    public Timestamp getFeedTime() {
        return userTweetItem.getTime();
    }

    public Long getFeedId() {
        return userTweetItem.getId();
    }


}
