package com.chitter.services;

import com.chitter.model.UserItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
        Long currUser = userID.get();
        return db.update("insert into followers (follower_id,celebrity_id) values (?,?);insert into following (follower_id,celebrity_id) values (?,?);", currUser, userItem.getId(), currUser, userItem.getId()) > 0;
    }

    public boolean unfollow(UserItem userItem) {
        Long currUser = userID.get();
        return db.update("delete from followers where follower_id=? AND celebrity_id=?; delete from following where follower_id=? AND celebrity_id=?;", currUser, userItem.getId(), currUser, userItem.getId()) > 0;
    }

    public List<UserItem> listFollowers(UserItem userItem) {
        return db.query("select * from users where id IN (select follower_id from followers where celebrity_id=?);", UserItem.rowMapper, userItem.getId());
    }

    public List<UserItem> listFollowed(UserItem userItem) {
        return db.query("select * from users where id IN (select celebrity_id from following where follower_id=?);", UserItem.rowMapper, userItem.getId());
    }
}
