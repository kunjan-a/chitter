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


    @RequestMapping("{id}/tweets")
    @ResponseBody
    public Map<Object, Object> fetchTweets(@PathVariable long id, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(id);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listTweets(userItem);
            response = ResponseUtil.getSuccessfulResponse(feeds);
            response.put("users", userStore.getUserItems(feeds));

            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(userItem));
            response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
        } else {
            response = ResponseUtil.getFailureResponse("No user exists with id:" + id);
        }
        response.put("userexists", validId);
        addCurrentUserItem(session, response);
        return response;
    }

    @RequestMapping("{id}/followers")
    @ResponseBody
    public Map<Object, Object> followers(@PathVariable long id, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(id);
        final boolean validId = userItem != null;
        if (validId) {
            List<UserItem> followers = followStore.listFollowers(userItem);
            response = ResponseUtil.getSuccessfulResponse(followers);
            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(followers));

        } else {
            response = ResponseUtil.getFailureResponse("No user exists with id:" + id);
        }
        response.put("userexists", validId);
        addCurrentUserItem(session, response);
        return response;
    }

    @RequestMapping("{id}/following")
    @ResponseBody
    public Map<Object, Object> following(@PathVariable long id, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(id);
        final boolean validId = userItem != null;
        if (validId) {
            List<UserItem> followed = followStore.listFollowed(userItem);
            response = ResponseUtil.getSuccessfulResponse(followed);
            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(followed));
            return response;
        } else {
            response = ResponseUtil.getFailureResponse("No user exists with id:" + id);
        }
        response.put("userexists", validId);
        addCurrentUserItem(session, response);
        return response;
    }

    @RequestMapping("{id}/feeds")
    @ResponseBody
    public Map<Object, Object> feeds(@PathVariable long id, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(id);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listFeeds(userItem);
            response = ResponseUtil.getSuccessfulResponse(feeds);
            response.put("users", userStore.getUserItems(feeds));
            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(userItem));
            response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
            return response;
        } else {
            response = ResponseUtil.getFailureResponse("No user exists with id:" + id);
        }
        response.put("userexists", validId);
        addCurrentUserItem(session, response);
        return response;
    }

    @RequestMapping("/tweet/{id}")
    @ResponseBody
    public Map<Object, Object> getFeedItemForTweet(@PathVariable long id, @RequestParam Long userId, HttpSession session) {
        List<FeedItem> feeds = tweetStore.getFeedsForTweet(id, userId);
        if (feeds != null) {
            Map<Object, Object> response = ResponseUtil.getSuccessfulResponse(feeds);
            response.put("users", userStore.getUserItems(feeds));
            response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
            return response;
        } else {
            return ResponseUtil.getFailureResponse("No tweet exists with the id:" + id);
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
