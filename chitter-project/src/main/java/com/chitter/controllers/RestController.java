package com.chitter.controllers;

import com.chitter.model.FeedItem;
import com.chitter.model.UserItem;
import com.chitter.services.FollowStore;
import com.chitter.services.TweetStore;
import com.chitter.services.UserStore;
import com.chitter.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 27/7/12
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("rest")
public class RestController {
    private final TweetStore tweetStore;
    private final UserStore userStore;
    private final FollowStore followStore;

    @Autowired
    public RestController(TweetStore tweetStore, UserStore userStore, FollowStore followStore) {
        this.tweetStore = tweetStore;
        this.userStore = userStore;
        this.followStore = followStore;
    }


    @RequestMapping("{userId}/tweets")
    @ResponseBody
    public Map<Object, Object> fetchTweets(@PathVariable long userId, @RequestParam(required = false) Integer numResults, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listTweets(userItem, numResults);
            response = ResponseUtil.getSuccessfulResponse(feeds);
            response.put("users", userStore.getUserItems(feeds));

            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(userItem));
            response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
        } else {
            response = ResponseUtil.getFailureResponse("No user exists with id:" + userId);
        }
        response.put("userexists", validId);
        addCurrentUserItem(session, response);
        return response;
    }

    @RequestMapping("{userId}/tweetsBefore")
    @ResponseBody
    public Map<Object, Object> fetchTweetsBefore(@PathVariable long userId, @RequestParam(required = false) Integer numResults, @RequestParam(required = false) Timestamp beforeTime, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listTweetsBefore(userItem, numResults, beforeTime);
            response = ResponseUtil.getSuccessfulResponse(feeds);
            response.put("users", userStore.getUserItems(feeds));

            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(userItem));
            response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
        } else {
            response = ResponseUtil.getFailureResponse("No user exists with id:" + userId);
        }
        response.put("userexists", validId);
        addCurrentUserItem(session, response);
        return response;
    }

    @RequestMapping("{userId}/tweetsAfter")
    @ResponseBody
    public Map<Object, Object> fetchTweetsAfter(@PathVariable long userId, @RequestParam(required = false) Integer numResults, @RequestParam(required = false) Timestamp afterTime, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listTweetsAfter(userItem, numResults, afterTime);
            response = ResponseUtil.getSuccessfulResponse(feeds);
            response.put("users", userStore.getUserItems(feeds));

            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(userItem));
            response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
        } else {
            response = ResponseUtil.getFailureResponse("No user exists with id:" + userId);
        }
        response.put("userexists", validId);
        addCurrentUserItem(session, response);
        return response;
    }

    @RequestMapping("{userId}/followers")
    @ResponseBody
    public Map<Object, Object> followers(@PathVariable long userId, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<UserItem> followers = followStore.listFollowers(userItem);
            response = ResponseUtil.getSuccessfulResponse(followers);
            response.put("user", userItem);

            response.put("usersFollowed", followStore.currentFollows(followers));
            response.put("follows", followStore.currentFollows(userItem));

        } else {
            response = ResponseUtil.getFailureResponse("No user exists with id:" + userId);
        }
        response.put("userexists", validId);
        addCurrentUserItem(session, response);
        return response;
    }

    @RequestMapping("{userId}/following")
    @ResponseBody
    public Map<Object, Object> following(@PathVariable long userId, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<UserItem> followed = followStore.listFollowed(userItem);
            response = ResponseUtil.getSuccessfulResponse(followed);
            response.put("user", userItem);

            response.put("usersFollowed", followStore.currentFollows(followed));
            response.put("follows", followStore.currentFollows(userItem));
            return response;
        } else {
            response = ResponseUtil.getFailureResponse("No user exists with id:" + userId);
        }
        response.put("userexists", validId);
        addCurrentUserItem(session, response);
        return response;
    }

    @RequestMapping("{userId}/feedsAfter")
    @ResponseBody
    public Map<Object, Object> feedsAfter(@PathVariable long userId, @RequestParam(required = false) Integer numResults, @RequestParam(required = false) Timestamp afterTime, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listFeedsAfter(userItem, numResults, afterTime);
            response = ResponseUtil.getSuccessfulResponse(feeds);
            response.put("users", userStore.getUserItems(feeds));
            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(userItem));
            response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
            return response;
        } else {
            response = ResponseUtil.getFailureResponse("No user exists with id:" + userId);
        }
        response.put("userexists", validId);
        addCurrentUserItem(session, response);
        return response;
    }

    @RequestMapping("{userId}/feedsBefore")
    @ResponseBody
    public Map<Object, Object> feedsBefore(@PathVariable long userId, @RequestParam(required = false) Integer numResults, @RequestParam(required = false) Timestamp beforeTime, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listFeedsBefore(userItem, numResults, beforeTime);
            response = ResponseUtil.getSuccessfulResponse(feeds);
            response.put("users", userStore.getUserItems(feeds));
            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(userItem));
            response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
            return response;
        } else {
            response = ResponseUtil.getFailureResponse("No user exists with id:" + userId);
        }
        response.put("userexists", validId);
        addCurrentUserItem(session, response);
        return response;
    }

    @RequestMapping("{userId}/feeds")
    @ResponseBody
    public Map<Object, Object> feeds(@PathVariable long userId, @RequestParam(required = false) Integer numResults, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listFeeds(userItem, numResults);
            response = ResponseUtil.getSuccessfulResponse(feeds);
            response.put("users", userStore.getUserItems(feeds));
            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(userItem));
            response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
            return response;
        } else {
            response = ResponseUtil.getFailureResponse("No user exists with id:" + userId);
        }
        response.put("userexists", validId);
        addCurrentUserItem(session, response);
        return response;
    }

    @RequestMapping("/tweet/{tweetId}")
    @ResponseBody
    public Map<Object, Object> getFeedItemForTweet(@PathVariable long tweetId, @RequestParam Long userId, HttpSession session) {
        List<FeedItem> feeds = tweetStore.getFeedsForTweet(tweetId, userId);
        if (feeds != null) {
            Map<Object, Object> response = ResponseUtil.getSuccessfulResponse(feeds);
            response.put("users", userStore.getUserItems(feeds));
            response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
            return response;
        } else {
            return ResponseUtil.getFailureResponse("No tweet exists with the id:" + tweetId);
        }
    }

    private void addCurrentUserItem(HttpSession session, Map<Object, Object> response) {
        final Long currUserId = (Long) session.getAttribute("userID");
        UserItem currUserItem = null;
        if (currUserId != null) {
            currUserItem = userStore.getUserWithId(currUserId);
        }
        response.put("currUserItem", currUserItem);
    }


}
