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
public class UserItem {

    private Long id;
    private long tweetCount, followerCount, followingCount, favouriteCount;
    private String name;
    private String email;
    private String bio;
    private String website;
    private String location;

    public long getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(long favouriteCount) {
        this.favouriteCount = favouriteCount;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBio() {
        return bio!=null?bio:"";
    }

    public String getWebsite() {
        return website!=null?website:"";
    }

    public String getLocation() {
        return location!=null?location:"";
    }

    private String emailVerification;
    public static final String PROFILE_PIC_PATH = "/static/images/user/";
    public static final String DEFAULT_PROFILE_PIC = PROFILE_PIC_PATH + "default";

    public long getTweetCount() {
        return tweetCount;
    }

    public void setTweetCount(long tweetCount) {
        this.tweetCount = tweetCount;
    }

    public long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(long followerCount) {
        this.followerCount = followerCount;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(long followingCount) {
        this.followingCount = followingCount;
    }

    public static final RowMapper<UserItem> rowMapper = new RowMapper<UserItem>() {
        @Override
        public UserItem mapRow(ResultSet resultSet, int i) throws SQLException {
            return new UserItem(resultSet);
        }
    };

    public UserItem(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getLong("id");
        this.name = resultSet.getString("name");
        this.email = resultSet.getString("email");
        this.followerCount = resultSet.getLong("follower_count");
        this.followingCount = resultSet.getLong("following_count");
        this.tweetCount = resultSet.getLong("tweet_count");
        this.bio = resultSet.getString("bio");
        this.location = resultSet.getString("location");
        this.website = resultSet.getString("website");
        this.emailVerification = resultSet.getString("email_verification");
        this.favouriteCount = resultSet.getLong("favourite_count");
    }

    public UserItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoPath() {
        return id != null ? PROFILE_PIC_PATH + id : DEFAULT_PROFILE_PIC;
    }

}
