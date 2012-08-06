package com.chitter.services;

import com.chitter.model.UserItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;

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
    public SimpleJdbcTemplate db;

    @Autowired
    public FollowStore(@Qualifier("userID") ThreadLocal<Long> userID, @Qualifier("simpleJdbcTemplate") SimpleJdbcTemplate template) {
        this.userID = userID;
        db = template;
    }

    public boolean currentFollows(UserItem userItem) {
        Long currUserId = userID.get();
        if (currUserId == null)
            return false;
        return isFollower(currUserId, userItem.getId());
    }

    private boolean isFollower(long follower, long beingFollowed) {
        return db.queryForInt("Select count(*) from following where follower_id=? AND celebrity_id=?", follower, beingFollowed) == 1;
    }

    public boolean follow(UserItem userItem) {
        Long follower_Id = userID.get();
        try {
            long celebrity_id = userItem.getId();
            int result = db.update("insert into followers (follower_id,celebrity_id) values (?,?);" +
                    "insert into following (follower_id,celebrity_id) values (?,?);" +
                    "update users set follower_count=follower_count+1 where id=?;" +
                    "update users set following_count=following_count+1 where id=?;",
                    follower_Id, celebrity_id, follower_Id, celebrity_id,
                    celebrity_id, follower_Id);
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
        Long follower_id = userID.get();
        try {
            long celebrity_id = userItem.getId();
            int result = db.update("delete from followers where follower_id=? AND celebrity_id=?; " +
                    "delete from following where follower_id=? AND celebrity_id=?;"+
                    "update users set follower_count=follower_count-1 where id=?;" +
                    "update users set following_count=following_count-1 where id=?;",
                    follower_id, celebrity_id, follower_id, celebrity_id,
                    celebrity_id, follower_id);
            return true;
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return false;
        }
    }

    public List<UserItem> listFollowers(UserItem userItem) {
        return db.query("select * from users where id IN (select follower_id from followers where celebrity_id=?);",
                UserItem.rowMapper, userItem.getId());
    }

    public List<UserItem> listFollowed(UserItem userItem) {
        return db.query("select * from users where id IN (select celebrity_id from following where follower_id=?);",
                UserItem.rowMapper, userItem.getId());
    }
}
