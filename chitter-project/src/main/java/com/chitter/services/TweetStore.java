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


        UserTweetItem userTweetItem = UserTweetItemGenerator.getInstance(db).getNextTweetItem();
        userTweetItem.setUser_id(currUser);
        userTweetItem.setEvent_type(UserTweetItem.NEW_TWEET);

        tweetItem.setId(userTweetItem.getEvent_id());
        tweetItem.setUser_id(currUser);
        tweetItem.setTime(userTweetItem.getTime());

        db.update("insert into tweets (id, time, text, user_id) values(?,to_timestamp(?),?,?); " +
                "insert into user_tweets (id, user_id, event_type, event_id, time) values(?,?,CAST(? AS tweet_type_enum),?,to_timestamp(?));" +
                "update users set tweetCount=tweetCount+1 where id=?;",
                tweetItem.getId(), Double.valueOf(tweetItem.getTime()), tweetItem.getText(), tweetItem.getUser_id(),
                userTweetItem.getId(), userTweetItem.getUser_id(), userTweetItem.getEvent_type(), userTweetItem.getEvent_id(), Double.valueOf(userTweetItem.getTime()),
                currUser);

        return getTweetWithId(userTweetItem.getEvent_id());


    }

    private List<TweetItem> getTweetWithId(long tweet_id) {
        return db.query("select * from tweets where id=?",
                TweetItem.rowMapper,
                tweet_id);
    }


    public List<TweetItem> listTweets(UserItem userItem) {
        return db.query("select * from tweets where user_id = ?", TweetItem.rowMapper, userItem.getId());
    }

    public List<TweetItem> retweet(TweetItem tweetItem) {
        Long currUser = userID.get();
        Assert.notNull(currUser);

        try {
            tweetItem = getTweetWithId(tweetItem.getId()).get(0);
        } catch (DataAccessException e) {
            return null;
        }

        UserTweetItem userTweetItem = UserTweetItemGenerator.getInstance(db).getNextTweetItem(tweetItem.getId());
        userTweetItem.setUser_id(currUser);
        userTweetItem.setEvent_type(UserTweetItem.RE_TWEET);

        db.update("update tweets set retweets=retweets+1 where id=?;" +
                "insert into user_tweets (id, user_id, event_type, event_id, time) values(?,?,CAST(? AS tweet_type_enum),?,to_timestamp(?));" +
                "update users set tweetCount=tweetCount+1 where id=?;",
                tweetItem.getId(),
                userTweetItem.getId(), userTweetItem.getUser_id(), userTweetItem.getEvent_type(), userTweetItem.getEvent_id(), Double.valueOf(userTweetItem.getTime()),
                currUser);

        return getTweetWithId(tweetItem.getId());

    }

    public String listFeeds(UserItem userItem) {
        return null;
    }
}
