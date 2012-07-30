package com.chitter.services;

import com.chitter.model.TweetItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcOperations;
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
public class TweetStore {
    private final ThreadLocal<Long> userID;
    public SimpleJdbcTemplate db;

    @Autowired
    public TweetStore(@Qualifier("userID") ThreadLocal<Long> userID, SimpleJdbcTemplate template) {
        this.userID = userID;
        db = template;
    }

    public List<TweetItem> list() {
        return db.query("select id, description from tweets where user_id=? order by id asc",
                TweetItem.rowMapper,
                userID.get());
    }

    public TweetItem add(TweetItem tweetItem) {
        assert (userID.get() != null);
        JdbcOperations jdbcOperations = db.getJdbcOperations();
        // jdbcOperations.

        db.update("insert into tweets (id, time, text, user_id) values(?,?,?,?)", tweetItem.getId(), tweetItem.getTime(), tweetItem.getText(), userID.get());
        int id = db.queryForInt("select currval('tweets_id_seq');");//CALL IDENTITY()");
        return db.queryForObject("select id, description from tweets where id=?",
                TweetItem.rowMapper,
                id);
    }


}
