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
import org.springframework.util.Assert;

public class UserTweetItemGenerator {
    private Long nextTweetId, nextUser_tweetId;

    private static UserTweetItemGenerator instance;


    public static synchronized UserTweetItemGenerator getInstance(SimpleJdbcTemplate db) {
        if (instance == null)
            instance = new UserTweetItemGenerator(db);
        return instance;
    }

    public UserTweetItem getNextTweetItem(long tweetid) {
        Assert.isTrue(tweetid < nextTweetId);

        UserTweetItem userTweetItem = new UserTweetItem();
        userTweetItem.setEvent_id(tweetid);
        userTweetItem.setId(nextUser_tweetId++);
        userTweetItem.setTime(String.valueOf(System.currentTimeMillis() / 1000));

        return userTweetItem;
    }

    public UserTweetItem getNextTweetItem() {
        return getNextTweetItem(nextTweetId++);
    }


    private UserTweetItemGenerator(SimpleJdbcTemplate db) {
        nextTweetId = db.queryForLong("Select max(id) from tweets;") + 1;
        nextUser_tweetId = db.queryForLong(" Select max(id) from user_tweets;") + 1;
        //db.update("insert into test (entry) values ('userTweetItemGenerator');");
    }

}
