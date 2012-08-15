package com.chitter.services;

import com.chitter.model.FavouriteUserItem;
import com.chitter.model.UserItem;
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
public class FavouriteUserStore {
    private final ThreadLocal<Long> userID;
    public NamedParameterJdbcTemplate db;
    private static final String USER_ID = "user_id";
    private static final String FAV_USER_ID = "fav_user_id";
    private static final String FAV_USER_IDS = "fav_user_ids";

    @Autowired
    public FavouriteUserStore(@Qualifier("userID") ThreadLocal<Long> userID, @Qualifier("namedParameterJdbcTemplate") NamedParameterJdbcTemplate template) {
        this.userID = userID;
        this.db = template;
    }

    public List<UserItem> listFavourites(UserItem userItem) {
        String sql ="select favourite_user_id from favourite_users where user_id=:" + USER_ID;
        final List<Long> favourites = db.query(sql, new MapSqlParameterSource(USER_ID, userItem.getId()), new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("favourite_user_id");
            }
        });
        if(favourites.isEmpty())
            return new ArrayList<UserItem>(0);

        sql = "select * from users where id IN ( :" + FAV_USER_IDS + ");";
        return db.query(sql, new MapSqlParameterSource(FAV_USER_IDS, favourites), UserItem.rowMapper);

    }

    public boolean isCurrentsFavourite(UserItem userItem) {
        Long currUserId = userID.get();
        if (currUserId == null)
            return false;
        return isFavourite(new FavouriteUserItem(currUserId, userItem.getId()));
    }

    private boolean isFavourite(FavouriteUserItem checkFavouriteUserItem) {
        String sql = "Select count(*) from favourite_users where user_id=:" + USER_ID + " AND favourite_user_id=:" + FAV_USER_ID + ";";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, checkFavouriteUserItem.getUser_id());
        namedParameters.addValue(FAV_USER_ID, checkFavouriteUserItem.getFavourite_user_id());

        return db.queryForInt(sql, namedParameters) == 1;

    }

    public boolean markFavouriteOfCurrent(UserItem userItem) {
        Long currUserId = userID.get();
        if (currUserId == null)
            return false;
        return markFavouriteOf(currUserId,userItem);
    }

    private boolean markFavouriteOf(long userId, UserItem favourite) {
        FavouriteUserItem favouriteUserItem = new FavouriteUserItem(userId, favourite.getId());
        try {
            String sql = "insert into favourite_users (user_id,favourite_user_id) values (:" + USER_ID + ",:" + FAV_USER_ID + ");" +
                    "update users set favourite_count=favourite_count+1 where id=:" + USER_ID + ";";
            MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, favouriteUserItem.getUser_id());
            namedParameters.addValue(FAV_USER_ID, favouriteUserItem.getFavourite_user_id());

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

    public boolean unmarkFavouriteOfCurrent(UserItem userItem) {
        Long currUserId = userID.get();
        if (currUserId == null)
            return false;
        return unmarkFavouriteOf(currUserId, userItem);
    }

    private boolean unmarkFavouriteOf(long userId, UserItem favourite) {
        FavouriteUserItem favouriteUserItem = new FavouriteUserItem(userId, favourite.getId());
        try {
            String sql = "delete from favourite_users where user_id=:"+USER_ID+" AND favourite_user_id=:" +FAV_USER_ID +";"+
                    "update users set favourite_count=favourite_count-1 where id=:" + USER_ID + ";";
            MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, favouriteUserItem.getUser_id());
            namedParameters.addValue(FAV_USER_ID, favouriteUserItem.getFavourite_user_id());

            db.update(sql, namedParameters);
            return true;
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return false;
        }

    }

    public List<FavouriteUserItem> favouritesOfCurrent(List<UserItem> favUsers) {
        Long currUserId = userID.get();
        if (currUserId == null)
            return new ArrayList<FavouriteUserItem>();
        return favouritesOf(currUserId, favUsers);
    }

    private List<FavouriteUserItem> favouritesOf(long userId, List<UserItem> favUsers) {
        if(favUsers.isEmpty())
            return new ArrayList<FavouriteUserItem>();

        List<Long> favUserIds = new ArrayList<Long>();
        for (UserItem userItem : favUsers) {
            favUserIds.add(userItem.getId());
        }

        String sql = "select * from favourite_users where user_id=:" + USER_ID + " AND favourite_user_id IN (:" + FAV_USER_IDS + ");";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource(USER_ID, userId);
        namedParameters.addValue(FAV_USER_IDS, favUserIds);
        return db.query(sql, namedParameters, FavouriteUserItem.rowMapper);

    }
}
