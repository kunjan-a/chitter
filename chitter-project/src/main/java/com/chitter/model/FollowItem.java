package com.chitter.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 27/7/12
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class FollowItem {

    private long followerId, celebrityId;

    public FollowItem(long follower_id, long celebrityId) {
        this.followerId = follower_id;
        this.celebrityId = celebrityId;
    }

    public long getCelebrityId() {
        return celebrityId;
    }

    public void setCelebrityId(long celebrityId) {
        this.celebrityId = celebrityId;
    }

    public long getFollowerId() {
        return followerId;

    }

    public void setFollowerId(long followerId) {
        this.followerId = followerId;
    }

    public static final RowMapper<FollowItem> rowMapper = new RowMapper<FollowItem>() {
        @Override
        public FollowItem mapRow(ResultSet resultSet, int i) throws SQLException {
            return new FollowItem(resultSet);
        }
    };

    public FollowItem(ResultSet resultSet) throws SQLException {
        this.followerId = resultSet.getLong("follower_id");
        this.celebrityId = resultSet.getLong("celebrity_id");
    }

    public FollowItem() {
    }

}