package com.chitter.services;

import com.chitter.model.FollowItem;
import com.chitter.model.UserItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

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
public class FollowStore {
    private final ThreadLocal<Long> userID;
    public NamedParameterJdbcTemplate db;
    private final String FLWR_ID = "follower_id";
    private final String CLBR_ID = "celebrity_id";
    private final String CLBRTY_IDS = "celebrity_ids";

    @Autowired
    public FollowStore(@Qualifier("userID") ThreadLocal<Long> userID, @Qualifier("namedParameterJdbcTemplate") NamedParameterJdbcTemplate template) {
        this.userID = userID;
        this.db = template;
    }

    public boolean currentFollows(UserItem userItem) {
        Long currUserId = userID.get();
        if (currUserId == null)
            return false;
        return isFollower(new FollowItem(currUserId, userItem.getId()));
    }

    public List<FollowItem> currentFollows(List<UserItem> followed) {
        Long currUserId = userID.get();
        if (currUserId == null)
            return new ArrayList<FollowItem>();
        return isFollower(currUserId, followed);
    }

    private boolean isFollower(FollowItem checkFollowItem) {
        String sql = "Select count(*) from following where follower_id=:" + FLWR_ID + " AND celebrity_id=:" + CLBR_ID + ";";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(FLWR_ID, checkFollowItem.getFollowerId());
        namedParameters.addValue(CLBR_ID, checkFollowItem.getCelebrityId());

        return db.queryForInt(sql, namedParameters) == 1;
    }

    private List<FollowItem> isFollower(Long follower_id, List<UserItem> followed) {
        List<Long> celebrityIds = new ArrayList<Long>();
        for (UserItem userItem : followed) {
            celebrityIds.add(userItem.getId());
        }

        String sql = "select * from following where follower_id=:" + follower_id + " AND celebrity_id IN :" + CLBRTY_IDS + ";";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(this.FLWR_ID, follower_id);
        namedParameters.addValue(CLBRTY_IDS, celebrityIds);
        return db.query(sql, namedParameters, FollowItem.rowMapper);
    }

    public boolean follow(UserItem userItem) {
        FollowItem followItem = new FollowItem(userID.get(), userItem.getId());
        try {
            String sql = "insert into followers (follower_id,celebrity_id) values (:" + FLWR_ID + ",:" + CLBR_ID + ");" +
                    "insert into following (follower_id,celebrity_id) values (:" + FLWR_ID + ",:" + CLBR_ID + ");" +
                    "update users set follower_count=follower_count+1 where id=:" + CLBR_ID + ";" +
                    "update users set following_count=following_count+1 where id=:" + FLWR_ID + ";";
            MapSqlParameterSource namedParameters = new MapSqlParameterSource(FLWR_ID, followItem.getFollowerId());
            namedParameters.addValue(CLBR_ID, followItem.getCelebrityId());

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

    public boolean unfollow(UserItem userItem) {
        FollowItem followItem = new FollowItem(userID.get(), userItem.getId());
        try {
            String sql = "delete from followers where follower_id=:" + FLWR_ID + " AND celebrity_id=:" + CLBR_ID + "; " +
                    "delete from following where follower_id=:" + FLWR_ID + " AND celebrity_id=:" + CLBR_ID + ";" +
                    "update users set follower_count=follower_count-1 where id=:" + CLBR_ID + ";" +
                    "update users set following_count=following_count-1 where id=:" + FLWR_ID + ";";
            MapSqlParameterSource namedParameters = new MapSqlParameterSource(FLWR_ID, followItem.getFollowerId());
            namedParameters.addValue(CLBR_ID, followItem.getCelebrityId());
            db.update(sql, namedParameters);

            return true;
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return false;
        }
    }

    public List<UserItem> listFollowers(UserItem userItem) {
        String sql = "select * from users where id IN (select follower_id from followers where celebrity_id=:" + CLBR_ID + ");";
        return db.query(sql, new MapSqlParameterSource(CLBR_ID, userItem.getId()), UserItem.rowMapper);
    }

    public List<UserItem> listFollowed(UserItem userItem) {
        String sql = "select * from users where id IN (select celebrity_id from following where follower_id=:" + FLWR_ID + ");";
        return db.query(sql, new MapSqlParameterSource(FLWR_ID, userItem.getId()), UserItem.rowMapper);
    }

}
