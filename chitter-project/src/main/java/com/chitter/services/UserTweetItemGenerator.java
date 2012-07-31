package com.chitter.services;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 30/7/12
 * Time: 5:35 PM
 * To change this template use File | Settings | File Templates.
 */

import com.chitter.model.UserTweetItem;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class UserTweetItemGenerator {
    private Long tweetId, user_tweetId;

    private static UserTweetItemGenerator instance;


    public static synchronized UserTweetItem getNext(SimpleJdbcTemplate db) {
        if (instance == null)
            instance = new UserTweetItemGenerator(db);
        return instance.getNextTweetItem();
    }

    private UserTweetItem getNextTweetItem() {
        tweetId++;
        user_tweetId++;

        UserTweetItem userTweetItem = new UserTweetItem();
        userTweetItem.setEvent_id(tweetId);
        userTweetItem.setId(user_tweetId);
        userTweetItem.setTime(String.valueOf(System.currentTimeMillis()/1000));

        return userTweetItem;
    }


    private UserTweetItemGenerator(SimpleJdbcTemplate db) {
        tweetId = db.queryForLong("Select max(id) from tweets;");
        user_tweetId = db.queryForLong(" Select max(id) from user_tweets;");
    }

}
