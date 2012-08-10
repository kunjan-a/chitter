package com.chitter.services;

import com.chitter.model.FeedItem;
import com.chitter.model.TweetItem;
import com.chitter.model.UserItem;
import com.chitter.model.UserTweetItem;
import com.chitter.model.UserTweetItem.TweetEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
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
    private final String USR_TWEET_USER_ID = "usr_tweet_user_id";
    private final String TWEET_IDS = "tweet_ids";
    private final String CLBR_ID = "celebrity_id";
    private final String USR_TWEET_IDS = "user_tweet_ids";

    @Autowired
    public TweetStore(@Qualifier("userID") ThreadLocal<Long> userID, @Qualifier("namedParameterJdbcTemplate") NamedParameterJdbcTemplate template) {
        this.userID = userID;
        db = template;
    }

    public List<FeedItem> addTweet(TweetItem tweetItem) {
        Long currUser = userID.get();
        Assert.notNull(currUser);


        UserTweetItem userTweetItem = UserTweetItemGenerator.getInstance(db).getNextTweetItem();

        String sql = "insert into tweets (id, time, text, user_id) values(:" + TWEET_ID + ",:" + TIME + ",:" + TEXT + ",:" + USER_ID + "); " +
                "insert into user_tweets (id, user_id, event_type, event_id, time) values(:" + USR_TWEET_ID + ",:" + USER_ID + ",CAST(:" + EVENT_TYPE + " AS tweet_type_enum),:" + TWEET_ID + ",:" + TIME + ");" +
                "update users set tweet_count=tweet_count+1 where id=:" + USER_ID + ";";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, currUser);
        namedParameters.addValue(TWEET_ID, userTweetItem.getEvent_id());
        namedParameters.addValue(TIME, userTweetItem.getTime());
        namedParameters.addValue(TEXT, tweetItem.getText());
        namedParameters.addValue(USR_TWEET_ID, userTweetItem.getId());
        namedParameters.addValue(EVENT_TYPE, TweetEventType.NEW_TWEET.toString());

        db.update(sql, namedParameters);

        TweetItem addedTweetItem = getTweetWithId(userTweetItem.getEvent_id());
        UserTweetItem addedUserTweetItem = getUserTweetWithId(userTweetItem.getId());
        addFeed(addedUserTweetItem);
        FeedItem addedFeedItem = new FeedItem(addedTweetItem, addedUserTweetItem);
        List<FeedItem> feeds = new ArrayList<FeedItem>(1);
        feeds.add(addedFeedItem);
        return feeds;
    }

    private TweetItem getTweetWithId(long tweet_id) {
        String sql = "select * from tweets where id=:" + TWEET_ID;
        return db.queryForObject(sql, new MapSqlParameterSource(TWEET_ID, tweet_id), TweetItem.rowMapper);
    }

    private UserTweetItem getUserTweetWithId(long user_tweet_id) {
        String sql = "select * from user_tweets where id=:" + USR_TWEET_ID;
        return db.queryForObject(sql, new MapSqlParameterSource(USR_TWEET_ID, user_tweet_id), UserTweetItem.rowMapper);
    }

    public List<FeedItem> listTweets(UserItem userItem) {
        Assert.notNull(userItem);
        String sql = "select * from user_tweets where user_id=:" + USER_ID;
        List<UserTweetItem> userTweetItems = db.query(sql, new MapSqlParameterSource(USER_ID, userItem.getId()), UserTweetItem.rowMapper);

        if (userTweetItems.isEmpty())
            return new ArrayList<FeedItem>();

        List<Long> tweetIds = new ArrayList<Long>(userTweetItems.size());
        for (UserTweetItem userTweetItem : userTweetItems) {
            tweetIds.add(userTweetItem.getEvent_id());
        }
        sql = "select * from tweets where id in (:" + TWEET_IDS + ");";
        List<TweetItem> tweetItems = db.query(sql, new MapSqlParameterSource(TWEET_IDS, tweetIds), TweetItem.rowMapper);

        List<FeedItem> feeds = new ArrayList<FeedItem>(userTweetItems.size());
        Assert.isTrue(tweetIds.size() == userTweetItems.size());
        for (int i = 0; i < userTweetItems.size(); i++) {
            UserTweetItem userTweetItem = userTweetItems.get(i);
            TweetItem tweetItem = tweetItems.get(i);
            Assert.isTrue(userTweetItem.getEvent_id() == tweetItem.getId());
            feeds.add(new FeedItem(tweetItem, userTweetItem));
        }
        return feeds;
    }

    public List<FeedItem> retweet(TweetItem tweetItem) {
        Long currUser = userID.get();
        Assert.notNull(currUser);

        try {
            tweetItem = getTweetWithId(tweetItem.getId());
        } catch (DataAccessException e) {
            return null;
        }

        if (tweetItem.getUser_id() == currUser)
            return null;

        UserTweetItem userTweetItem = UserTweetItemGenerator.getInstance(db).getNextTweetItem(tweetItem.getId());

        String sql = "update tweets set retweets=retweets+1 where id=:" + TWEET_ID + ";" +
                "insert into user_tweets (id, user_id, event_type, event_id, time) values(:" + USR_TWEET_ID + ",:" + USER_ID + ",CAST(:" + EVENT_TYPE + " AS tweet_type_enum),:" + TWEET_ID + ",:" + TIME + ");" +
                "update users set tweet_count=tweet_count+1 where id=:" + USER_ID + ";";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, currUser);
        namedParameters.addValue(TWEET_ID, tweetItem.getId());
        namedParameters.addValue(TIME, userTweetItem.getTime());
        namedParameters.addValue(USR_TWEET_ID, userTweetItem.getId());
        namedParameters.addValue(EVENT_TYPE, TweetEventType.RE_TWEET.toString());

        db.update(sql, namedParameters);

        addFeed(userTweetItem);
        TweetItem addedTweetItem = getTweetWithId(userTweetItem.getEvent_id());
        UserTweetItem addedUserTweetItem = getUserTweetWithId(userTweetItem.getId());
        FeedItem addedFeedItem = new FeedItem(addedTweetItem, addedUserTweetItem);
        List<FeedItem> feeds = new ArrayList<FeedItem>(1);
        feeds.add(addedFeedItem);
        return feeds;
    }

    private void addFeed(UserTweetItem userTweetItem) {
        String sql = "insert into feeds (user_id, user_tweet_id, user_tweet_user_id, user_tweet_time) values(:" + USER_ID + ",:" + USR_TWEET_ID + ",:" + USER_ID + ",:" + TIME + "); ";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, userTweetItem.getUser_id());
        namedParameters.addValue(USR_TWEET_ID, userTweetItem.getId());
        namedParameters.addValue(TIME, userTweetItem.getTime());

        db.update(sql, namedParameters);

        updateFollowers(userTweetItem);
    }

    private void updateFollowers(UserTweetItem userTweetItem) {
        List<Long> follower_ids = db.query("select follower_id from followers where celebrity_id=:" + CLBR_ID, new MapSqlParameterSource(CLBR_ID, userTweetItem.getUser_id()), new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("follower_id");
            }
        });

        String sql = "insert into feeds (user_id, user_tweet_id, user_tweet_user_id, user_tweet_time) values(:" + USER_ID + ",:" + USR_TWEET_ID + ",:" + USR_TWEET_USER_ID + ",:" + TIME + "); ";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USR_TWEET_USER_ID, userTweetItem.getUser_id());
        namedParameters.addValue(USR_TWEET_ID, userTweetItem.getId());
        namedParameters.addValue(TIME, userTweetItem.getTime());

        for (Long follower_id : follower_ids) {
            namedParameters.addValue(USER_ID, follower_id);
            db.update(sql, namedParameters);
        }
    }


    public List<FeedItem> listFeeds(UserItem userItem) {
        Assert.notNull(userItem);
        return listFeeds(userItem.getId());
    }

    public List<FeedItem> listFeeds(long userId) {
        String sql = "select user_tweet_id from feeds where user_id=:" + USER_ID;
        List<Long> userTweetIds = db.query(sql, new MapSqlParameterSource(USER_ID, userId), new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("user_tweet_id");  //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        if (userTweetIds.isEmpty())
            return new ArrayList<FeedItem>();


        sql = "select * from user_tweets where id in (:" + USR_TWEET_IDS + ")";
        List<UserTweetItem> userTweetItems = db.query(sql, new MapSqlParameterSource(USR_TWEET_IDS, userTweetIds), UserTweetItem.rowMapper);


        List<Long> tweetIds = new ArrayList<Long>(userTweetItems.size());
        for (UserTweetItem userTweetItem : userTweetItems) {
            tweetIds.add(userTweetItem.getEvent_id());
        }
        sql = "select * from tweets where id in (:" + TWEET_IDS + ");";
        List<TweetItem> tweetItems = db.query(sql, new MapSqlParameterSource(TWEET_IDS, tweetIds), TweetItem.rowMapper);

        List<FeedItem> feeds = new ArrayList<FeedItem>(userTweetItems.size());
        int tweetIndex = 0;
        for (int i = 0; i < userTweetItems.size(); i++) {
            UserTweetItem userTweetItem = userTweetItems.get(i);
            TweetItem tweetItem = tweetItems.get(tweetIndex);
            while (userTweetItem.getEvent_id() != tweetItem.getId()) {
                tweetIndex++;
                tweetItem = tweetItems.get(tweetIndex);
            }
            feeds.add(new FeedItem(tweetItem, userTweetItem));
        }
        return feeds;
    }

    public List<Long> retweetedByCurrent(List<FeedItem> feeds) {
        return retweetedByCurrent(userID.get(), feeds);
    }

    private List<Long> retweetedByCurrent(Long userId, List<FeedItem> feeds) {
        if (userId == null || feeds == null || feeds.isEmpty())
            return new ArrayList<Long>(0);

        HashSet<Long> tweetIds = new HashSet<Long>();
        for (FeedItem feed : feeds) {
            tweetIds.add(feed.getTweetId());
        }

        String sql = "select event_id from user_tweets where user_id=:" + USR_TWEET_USER_ID + " AND event_type=CAST(:" + EVENT_TYPE + " AS tweet_type_enum) AND event_id in (:" + TWEET_IDS + ")";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USR_TWEET_USER_ID, userId);
        namedParameters.addValue(EVENT_TYPE, TweetEventType.RE_TWEET.toString());
        namedParameters.addValue(TWEET_IDS, tweetIds);

        return db.query(sql, namedParameters, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("event_id");
            }
        });
    }

    public List<FeedItem> getFeedsForTweetId(long tweetId) {
        String sql = "select * from user_tweets where event_id=:" + TWEET_ID;
        List<UserTweetItem> userTweetItems = db.query(sql, new MapSqlParameterSource(TWEET_ID, tweetId), UserTweetItem.rowMapper);

        if (userTweetItems.isEmpty())
            return null;

        sql = "select * from tweets where id=:" + TWEET_ID;
        List<TweetItem> tweetItems = db.query(sql, new MapSqlParameterSource(TWEET_IDS, tweetId), TweetItem.rowMapper);

        List<FeedItem> feeds = new ArrayList<FeedItem>(userTweetItems.size());
        int tweetIndex = 0;
        for (int i = 0; i < userTweetItems.size(); i++) {
            UserTweetItem userTweetItem = userTweetItems.get(i);
            TweetItem tweetItem = tweetItems.get(tweetIndex);
            while (userTweetItem.getEvent_id() != tweetItem.getId()) {
                tweetIndex++;
                tweetItem = tweetItems.get(tweetIndex);
            }
            feeds.add(new FeedItem(tweetItem, userTweetItem));
        }
        return feeds;
    }
}

