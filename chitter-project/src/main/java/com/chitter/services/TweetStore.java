package com.chitter.services;

import com.chitter.model.TweetItem;
import com.chitter.model.UserItem;
import com.chitter.model.UserTweetItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 27/7/12
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TweetStore {
    private final ThreadLocal<Long> userID;
    public SimpleJdbcTemplate db;

    @Autowired
    public TweetStore(@Qualifier("userID") ThreadLocal<Long> userID, @Qualifier("simpleJdbcTemplate") SimpleJdbcTemplate template) {
        this.userID = userID;
        db = template;
    }

    public List<TweetItem> add(TweetItem tweetItem) {
        Long currUser = userID.get();
        Assert.notNull(currUser);


        UserTweetItem userTweetItem = UserTweetItemGenerator.getNext(db);

        db.update("insert into tweets (id, time, text, user_id) values(?,to_timestamp(?),?,?); " +
                "insert into user_tweets (id, user_id, event_type, event_id, time) values(?,?,CAST(? AS tweet_type_enum),?,to_timestamp(?));",
                userTweetItem.getEvent_id(), Double.valueOf(userTweetItem.getTime()), tweetItem.getText(), currUser,
                userTweetItem.getId(), currUser, UserTweetItem.NEW_TWEET, userTweetItem.getEvent_id(), Double.valueOf(userTweetItem.getTime()));

        return db.query("select * from tweets where id=?",
                TweetItem.rowMapper,
                userTweetItem.getEvent_id());
    }


    public List<TweetItem> fetchTweetsBy(UserItem userItem) {
        return db.query("select * from tweets where user_id = ?", TweetItem.rowMapper, userItem.getId());
    }

    public TweetItem retweet(TweetItem tweetItem) {
        Long currUser = userID.get();
        Assert.notNull(currUser);

        try {
            tweetItem = db.queryForObject("select * from tweets where id=?",
                    TweetItem.rowMapper,
                    tweetItem.getId());
        } catch (DataAccessException e) {
            return null;
        }

        UserTweetItem userTweetItem = UserTweetItemGenerator.getNext(db);
        userTweetItem.setEvent_id(tweetItem.getId());
        userTweetItem.setUser_id(currUser);
        userTweetItem.setEvent_type(UserTweetItem.RE_TWEET);

        db.update("update tweets set retweets= (select retweets from tweets where id=?)+1 where id=?;" +
                "insert into user_tweets (id, user_id, event_type, event_id, time) values(?,?,CAST(? AS tweet_type_enum),?,to_timestamp(?));",
                tweetItem.getId(), tweetItem.getId(),
                userTweetItem.getId(), userTweetItem.getUser_id(), userTweetItem.getEvent_type(), userTweetItem.getEvent_id(), Double.valueOf(userTweetItem.getTime()));

        return tweetItem = db.queryForObject("select * from tweets where id=?",
                TweetItem.rowMapper,
                tweetItem.getId());

    }
}
