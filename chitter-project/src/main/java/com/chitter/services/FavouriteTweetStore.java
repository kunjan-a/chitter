package com.chitter.services;

import com.chitter.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 27/7/12
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class FavouriteTweetStore {
    private final ThreadLocal<Long> userID;
    private final TweetStore tweetStore;
    public NamedParameterJdbcTemplate db;
    private final String USER_ID = "user_id";
    private final String TWEET_ID = "celebrity_id";
    private final String TWEET_IDS = "tweet_ids";


    @Autowired
    public FavouriteTweetStore(@Qualifier("userID") ThreadLocal<Long> userID, TweetStore tweetStore, @Qualifier("namedParameterJdbcTemplate") NamedParameterJdbcTemplate template) {
        this.userID = userID;
        this.tweetStore = tweetStore;
        this.db = template;
    }

    public List<FavouriteTweetItem> favouritesOfCurrent(List<FeedItem> feeds) {
        Long currUserId = userID.get();
        if (currUserId == null)
            return new ArrayList<FavouriteTweetItem>();

        return favouritesOfUser(currUserId, feeds);
    }

    private List<FavouriteTweetItem> favouritesOfUser(long userId, List<FeedItem> feeds) {
        if(feeds==null || feeds.isEmpty())
            return new ArrayList<FavouriteTweetItem>(0);
        List<Long> tweetIds = new ArrayList<Long>();
        for (FeedItem feedItem : feeds) {
            tweetIds.add(feedItem.getTweetId());
        }

        String sql = "select * from favourite_tweets where user_id=:" + USER_ID + " AND tweet_id IN (:" + TWEET_IDS + ");";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, userId);
        namedParameters.addValue(TWEET_IDS, tweetIds);
        return db.query(sql, namedParameters, FavouriteTweetItem.rowMapper);

    }

    public boolean markFavouriteOfCurrent(TweetItem tweetItem) {
        Long currUserId = userID.get();
        if (currUserId == null)
            return false;
        return markFavouriteOf(currUserId,tweetItem);
    }

    private boolean markFavouriteOf(long userId, TweetItem tweetItem) {

        FavouriteTweetItem favouriteTweetItem = new FavouriteTweetItem(userId, tweetItem.getId());
        try {
            String sql = "insert into favourite_tweets (user_id,tweet_id) values (:" + USER_ID + ",:" + TWEET_ID + ");" +
                    "update tweets set favourites=favourites+1 where id=:" + TWEET_ID + ";";
            MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, favouriteTweetItem.getUser_id());
            namedParameters.addValue(TWEET_ID, favouriteTweetItem.getTweet_id());

            int result = db.update(sql, namedParameters);
            return result > 0;
        } catch (DuplicateKeyException alreadyFollowingException) {
            return true;
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return false;
        }
    }


    public boolean unmarkFavouriteOfCurrent(TweetItem tweetItem) {
        Long currUserId = userID.get();
        if (currUserId == null)
            return false;
        return unmarkFavouriteOf(currUserId, tweetItem);
    }

    private boolean unmarkFavouriteOf(long userId, TweetItem tweetItem) {
        FavouriteTweetItem favouriteTweetItem = new FavouriteTweetItem(userId, tweetItem.getId());
        try {
            String sql = "delete from favourite_tweets where user_id=:" +  USER_ID + " AND tweet_id=:" + TWEET_ID + ";" +
                    "update tweets set favourites=favourites-1 where id=:" + TWEET_ID + ";";
            MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, favouriteTweetItem.getUser_id());
            namedParameters.addValue(TWEET_ID, favouriteTweetItem.getTweet_id());

            db.update(sql, namedParameters);
            return true;
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return false;
        }
    }

    public List<FeedItem> listFavourites(UserItem userItem) {
        String sql ="select tweet_id from favourite_tweets where user_id=:" + USER_ID;
        final List<Long> favourites = db.query(sql, new MapSqlParameterSource(USER_ID, userItem.getId()), new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("tweet_id");
            }
        });
        if(favourites.isEmpty())
            return new ArrayList<FeedItem>(0);

        sql = "select * from user_tweets where event_id IN ( :" + TWEET_IDS + ") order by time DESC;";
        final List<UserTweetItem> user_tweets = db.query(sql, new MapSqlParameterSource(TWEET_IDS, favourites), UserTweetItem.rowMapper);


        return tweetStore.getFeedItems(user_tweets);
    }
}
