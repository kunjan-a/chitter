package com.chitter.services;

import com.chitter.model.TweetItem;
import com.chitter.model.UserItem;
import com.chitter.model.UserTweetItem;
import com.chitter.model.UserTweetItem.TweetEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
    public NamedParameterJdbcTemplate db;
    private final String USER_ID = "user_id";
    private final String TWEET_ID = "tweet_id";
    private final String TIME = "time";
    private final String TEXT = "text";
    private final String USR_TWEET_ID = "usr_tweet_id";
    private final String EVENT_TYPE = "event_type";

    @Autowired
    public TweetStore(@Qualifier("userID") ThreadLocal<Long> userID, @Qualifier("namedParameterJdbcTemplate") NamedParameterJdbcTemplate template) {
        this.userID = userID;
        db = template;
    }

    public List<TweetItem> add(TweetItem tweetItem) {
        Long currUser = userID.get();
        Assert.notNull(currUser);


        UserTweetItem userTweetItem = UserTweetItemGenerator.getInstance(db).getNextTweetItem();

        String sql = "insert into tweets (id, time, text, user_id) values(:" + TWEET_ID + ",to_timestamp(:" + TIME + "),:" + TEXT + ",:" + USER_ID + "); " +
                "insert into user_tweets (id, user_id, event_type, event_id, time) values(:" + USR_TWEET_ID + ",:" + USER_ID + ",CAST(:" + EVENT_TYPE + " AS tweet_type_enum),:" + TWEET_ID + ",to_timestamp(:" + TIME + "));" +
                "update users set tweet_count=tweet_count+1 where id=:" + USER_ID + ";";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, currUser);
        namedParameters.addValue(TWEET_ID, userTweetItem.getEvent_id());
        namedParameters.addValue(TIME, Double.valueOf(userTweetItem.getTime()));
        namedParameters.addValue(TEXT, tweetItem.getText());
        namedParameters.addValue(USR_TWEET_ID, userTweetItem.getId());
        namedParameters.addValue(EVENT_TYPE, TweetEventType.NEW_TWEET.toString());

        db.update(sql, namedParameters);

        return getTweetWithId(userTweetItem.getEvent_id());
    }

    private List<TweetItem> getTweetWithId(long tweet_id) {
        String sql = "select * from tweets where id=:" + TWEET_ID;
        return db.query(sql, new MapSqlParameterSource(TWEET_ID, tweet_id), TweetItem.rowMapper);
    }


    public List<TweetItem> listTweets(UserItem userItem) {
        String sql = "select * from tweets where user_id=:" + USER_ID;
        return db.query(sql, new MapSqlParameterSource(USER_ID, userItem.getId()), TweetItem.rowMapper);
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

        String sql = "update tweets set retweets=retweets+1 where id=:" + TWEET_ID + ";" +
                "insert into user_tweets (id, user_id, event_type, event_id, time) values(:" + USR_TWEET_ID + ",:" + USER_ID + ",CAST(:" + EVENT_TYPE + " AS tweet_type_enum),:" + TWEET_ID + ",to_timestamp(" + TIME + "));" +
                "update users set tweet_count=tweet_count+1 where id=:" + USER_ID + ";";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, currUser);
        namedParameters.addValue(TWEET_ID, tweetItem.getId());
        namedParameters.addValue(TIME, Double.valueOf(userTweetItem.getTime()));
        namedParameters.addValue(USR_TWEET_ID, userTweetItem.getId());
        namedParameters.addValue(EVENT_TYPE, TweetEventType.RE_TWEET.toString());

        db.update(sql, namedParameters);

        return getTweetWithId(tweetItem.getId());

    }

    public String listFeeds(UserItem userItem) {
        return null;
    }
}
