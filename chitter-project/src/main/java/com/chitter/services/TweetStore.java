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
import java.sql.Timestamp;
import java.util.*;

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
    private static final String USER_ID = "user_id";
    private static final String TWEET_ID = "tweet_id";
    private static final String TIME = "time";
    private static final String TEXT = "text";
    private static final String USR_TWEET_ID = "usr_tweet_id";
    private static final String EVENT_TYPE = "event_type";
    private static final String USR_TWEET_USER_ID = "usr_tweet_user_id";
    private static final String TWEET_IDS = "tweet_ids";
    private static final String CLBR_ID = "celebrity_id";
    private static final String USR_TWEET_IDS = "user_tweet_ids";
    private static final String LIMIT = "limit";
    private static final Integer DEF_LIMIT = 30;
    private static final int ONE_HOUR = 60 * 60 * 1000;
    private static final String AFTER_TIME = "after_time";
    private static final String BEFORE_TIME = "before_time";

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

        TweetItem addedTweetItem = getTweetWithId(userTweetItem.getEvent_id(), currUser);
        UserTweetItem addedUserTweetItem = getUserTweetWithId(userTweetItem.getId(), currUser);
        addFeed(addedUserTweetItem);
        FeedItem addedFeedItem = new FeedItem(addedTweetItem, addedUserTweetItem);
        List<FeedItem> feeds = new ArrayList<FeedItem>(1);
        feeds.add(addedFeedItem);
        return feeds;
    }

    public TweetItem getTweetWithId(long tweet_id, Long userId) {
        String sql = "select * from tweets where id=:" + TWEET_ID;
        return db.queryForObject(sql, new MapSqlParameterSource(TWEET_ID, tweet_id), TweetItem.rowMapper);
    }

    private UserTweetItem getUserTweetWithId(long user_tweet_id, Long userId) {
        String sql = "select * from user_tweets where id=:" + USR_TWEET_ID;
        return db.queryForObject(sql, new MapSqlParameterSource(USR_TWEET_ID, user_tweet_id), UserTweetItem.rowMapper);
    }

    public List<FeedItem> listTweetsBefore(UserItem userItem, Integer numTweets, Timestamp beforeTime) {
        if (beforeTime == null) beforeTime = new Timestamp(System.currentTimeMillis());

        final String condition = " AND time < :" + BEFORE_TIME;
        final MapSqlParameterSource namedParameters = new MapSqlParameterSource(BEFORE_TIME, beforeTime);
        return listTweets(userItem, numTweets, condition, namedParameters);
    }

    public List<FeedItem> listTweetsAfter(UserItem userItem, Integer numTweets, Timestamp afterTime) {
        if (afterTime == null) afterTime = new Timestamp(System.currentTimeMillis() - ONE_HOUR);

        final String condition = " AND time > :" + AFTER_TIME;
        final MapSqlParameterSource namedParameters = new MapSqlParameterSource(AFTER_TIME, afterTime);
        return listTweets(userItem, numTweets, condition, namedParameters);
    }

    public List<FeedItem> listTweets(UserItem userItem, Integer numTweets) {
        return listTweets(userItem, numTweets, null, null);
    }

    private List<FeedItem> listTweets(UserItem userItem, Integer numTweets, String condition, MapSqlParameterSource namedParameters) {
        if (numTweets == null) numTweets = DEF_LIMIT;
        if (condition == null) condition = "";
        if (namedParameters == null) namedParameters = new MapSqlParameterSource();
        Assert.notNull(userItem);

        String sql = "select * from user_tweets where user_id=:" + USER_ID + " " + condition + " order by time DESC limit :" + LIMIT;
        namedParameters.addValue(USER_ID, userItem.getId()).addValue(LIMIT, numTweets);
        List<UserTweetItem> userTweetItems = db.query(sql, namedParameters, UserTweetItem.rowMapper);
        return getFeedItems(userTweetItems);
    }

    public List<FeedItem> getFeedItems(List<UserTweetItem> userTweetItems) {
        if (userTweetItems.isEmpty())
            return new ArrayList<FeedItem>();

        List<TweetItem> tweetItems = getTweetsForUserTweets(userTweetItems);
        HashMap<Long,TweetItem> tweetItemMap = new HashMap<Long,TweetItem>();
        for (TweetItem tweetItem : tweetItems) {
            tweetItemMap.put(tweetItem.getId(),tweetItem);
        }

        List<FeedItem> feeds = new ArrayList<FeedItem>(userTweetItems.size());
        for (int i = 0; i < userTweetItems.size(); i++) {
            UserTweetItem userTweetItem = userTweetItems.get(i);
            TweetItem tweetItem = tweetItemMap.get(userTweetItem.getEvent_id());
            feeds.add(new FeedItem(tweetItem, userTweetItem));
        }

        return feeds;
    }

    private List<TweetItem> getTweetsForUserTweets(List<UserTweetItem> userTweetItems) {
        Set<Long> tweetIds = new HashSet<Long>(userTweetItems.size());
        for (UserTweetItem userTweetItem : userTweetItems) {
            tweetIds.add(userTweetItem.getEvent_id());
        }
        String sql = "select * from tweets where id in (:" + TWEET_IDS + ");";
        return db.query(sql, new MapSqlParameterSource(TWEET_IDS, tweetIds), TweetItem.rowMapper);

    }

    public List<FeedItem> retweet(TweetItem tweetItem) {
        Long currUser = userID.get();
        Assert.notNull(currUser);

        try {
            tweetItem = getTweetWithId(tweetItem.getId(), currUser);
        } catch (DataAccessException e) {
            return null;
        }

        if (tweetItem.getUser_id() == currUser || retweetedBy(currUser, tweetItem) == null)
            return null;

        UserTweetItem userTweetItem = UserTweetItemGenerator.getInstance(db).getNextTweetItem(tweetItem.getId());

        String sql = "update tweets set retweets=retweets+1 where id=:" + TWEET_ID + ";" +
                "insert into user_tweets (id, user_id, event_type, event_id, time) values(:" + USR_TWEET_ID + ",:" + USER_ID + ",CAST(:" + EVENT_TYPE + " AS tweet_type_enum),:" + TWEET_ID + ",:" + TIME + ");" +
                "insert into retweets (tweet_id, user_id) values( :" + TWEET_ID + ",:" + USER_ID + ");" +
                "update users set tweet_count=tweet_count+1 where id=:" + USER_ID + ";";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, currUser);
        namedParameters.addValue(TWEET_ID, tweetItem.getId());
        namedParameters.addValue(TIME, userTweetItem.getTime());
        namedParameters.addValue(USR_TWEET_ID, userTweetItem.getId());
        namedParameters.addValue(EVENT_TYPE, TweetEventType.RE_TWEET.toString());

        db.update(sql, namedParameters);

        TweetItem addedTweetItem = getTweetWithId(userTweetItem.getEvent_id(), currUser);
        UserTweetItem addedUserTweetItem = getUserTweetWithId(userTweetItem.getId(), currUser);
        addFeed(addedUserTweetItem);
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

    public List<FeedItem> listFeedsBefore(UserItem userItem, Integer numFeeds, Timestamp beforeTime) {
        if (beforeTime == null) beforeTime = new Timestamp(System.currentTimeMillis());

        final String condition = " AND user_tweet_time < :" + BEFORE_TIME;
        final MapSqlParameterSource namedParameters = new MapSqlParameterSource(BEFORE_TIME, beforeTime);
        return listTweets(userItem, numFeeds, condition, namedParameters);
    }

    public List<FeedItem> listFeedsAfter(UserItem userItem, Integer numFeeds, Timestamp afterTime) {
        if (afterTime == null) afterTime = new Timestamp(System.currentTimeMillis() - ONE_HOUR);

        final String condition = " AND user_tweet_time > :" + AFTER_TIME;
        final MapSqlParameterSource namedParameters = new MapSqlParameterSource(AFTER_TIME, afterTime);
        return listTweets(userItem, numFeeds, condition, namedParameters);
    }

    public List<FeedItem> listFeeds(UserItem userItem, Integer numFeeds) {
        return listFeeds(userItem, numFeeds, null, null);
    }

    public List<FeedItem> listFeeds(UserItem userItem, Integer numFeeds, String condition, MapSqlParameterSource namedParameters) {
        if (numFeeds == null) numFeeds = DEF_LIMIT;
        if (condition == null) condition = "";
        if (namedParameters == null) namedParameters = new MapSqlParameterSource();
        Assert.notNull(userItem);

        String sql = "select user_tweet_id from feeds where user_id=:" + USER_ID + " " + condition + " order by user_tweet_time DESC limit :" + LIMIT;
        namedParameters.addValue(USER_ID, userItem.getId()).addValue(LIMIT, numFeeds);
        List<Long> userTweetIds = db.query(sql, namedParameters, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("user_tweet_id");
            }
        });

        if (userTweetIds.isEmpty()) return new ArrayList<FeedItem>();

        sql = "select * from user_tweets where id in (:" + USR_TWEET_IDS + ") order by time DESC ";
        List<UserTweetItem> userTweetItems = db.query(sql, new MapSqlParameterSource(USR_TWEET_IDS, userTweetIds), UserTweetItem.rowMapper);

        return getFeedItems(userTweetItems);

    }

    public List<Long> retweetedByCurrent(List<FeedItem> feeds) {
        return retweetedBy(userID.get(), feeds);
    }

    private List<Long> retweetedBy(Long userId, List<FeedItem> feeds) {
        if (userId == null || feeds == null || feeds.isEmpty())
            return new ArrayList<Long>(0);

        HashSet<Long> tweetIds = new HashSet<Long>();
        for (FeedItem feed : feeds) {
            tweetIds.add(feed.getTweetId());
        }

        return retweetedBy(userId, tweetIds);
    }

    private List<Long> retweetedBy(Long userId, HashSet<Long> tweetIds) {
        String sql = "select tweet_id from retweets where user_id=:" + USR_TWEET_USER_ID + " AND tweet_id in (:" + TWEET_IDS + ")";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USR_TWEET_USER_ID, userId);
        namedParameters.addValue(TWEET_IDS, tweetIds);

        return db.query(sql, namedParameters, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("tweet_id");
            }
        });
    }

    private List<Long> retweetedBy(Long currUser, TweetItem tweetItem) {
        final HashSet<Long> tweetIds = new HashSet<Long>(1);
        tweetIds.add(tweetItem.getId());
        return retweetedBy(currUser, tweetIds);
    }

    public List<FeedItem> getFeedsForTweet(long tweetId, long originalTweeterId) {
        String sql = "select * from retweets where tweet_id=:" + TWEET_ID;
        final List<Long> retweeterIds = db.query(sql, new MapSqlParameterSource(TWEET_ID, tweetId), new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("user_id");
            }
        });

        //TODO: For sharding the user_tweets can be mulitple depending upon whether the original tweeter and retweeters are in same db or not.
        sql = "select * from user_tweets where event_id=:" + TWEET_ID;

        List<UserTweetItem> userTweetItems = db.query(sql, new MapSqlParameterSource(TWEET_ID, tweetId), UserTweetItem.rowMapper);

        if (userTweetItems.isEmpty())
            return null;

        sql = "select * from tweets where id=:" + TWEET_ID;
        List<TweetItem> tweetItems = db.query(sql, new MapSqlParameterSource(TWEET_ID, tweetId), TweetItem.rowMapper);

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

