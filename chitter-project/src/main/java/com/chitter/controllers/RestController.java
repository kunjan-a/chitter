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
        UserItem userItem = userStore.getUserWithId(id);
        if (userItem != null) {
            List<FeedItem> feeds = tweetStore.listTweets(userItem);
            Map<Object, Object> response = ResponseUtil.getSuccessfulResponse(feeds);
            response.put("users", userStore.getUserItems(feeds));

            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(userItem));
            response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
            return response;
        } else {
            Map<Object, Object> failureResponse = ResponseUtil.getFailureResponse("No user exists with id:" + id);
            failureResponse.put("userexists", userItem != null);
            return failureResponse;
        }
    }

    @RequestMapping("{id}/followers")
    @ResponseBody
    public Map<Object, Object> followers(@PathVariable long id, HttpSession session) {
        UserItem userItem = userStore.getUserWithId(id);
        if (userItem != null) {
            List<UserItem> followers = followStore.listFollowers(userItem);
            Map<Object, Object> response = ResponseUtil.getSuccessfulResponse(followers);
            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(followers));

            return response;
        } else {
            Map<Object, Object> failureResponse = ResponseUtil.getFailureResponse("No user exists with id:" + id);
            failureResponse.put("userexists", userItem != null);
            return failureResponse;
        }
    }

    @RequestMapping("{id}/following")
    @ResponseBody
    public Map<Object, Object> following(@PathVariable long id, HttpSession session) {
        UserItem userItem = userStore.getUserWithId(id);
        if (userItem != null) {
            List<UserItem> followed = followStore.listFollowed(userItem);
            Map<Object, Object> response = ResponseUtil.getSuccessfulResponse(followed);
            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(followed));
            return response;
        } else {
            Map<Object, Object> failureResponse = ResponseUtil.getFailureResponse("No user exists with id:" + id);
            failureResponse.put("userexists", userItem != null);
            return failureResponse;
        }
    }

    @RequestMapping("{id}/feeds")
    @ResponseBody
    public Map<Object, Object> feeds(@PathVariable long id, HttpSession session) {
        UserItem userItem = userStore.getUserWithId(id);
        if (userItem != null) {
            List<FeedItem> feeds = tweetStore.listFeeds(userItem);
            Map<Object, Object> response = ResponseUtil.getSuccessfulResponse(feeds);
            response.put("users", userStore.getUserItems(feeds));
            response.put("user", userItem);

            response.put("follows", followStore.currentFollows(userItem));
            response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
            return response;
        } else {
            Map<Object, Object> failureResponse = ResponseUtil.getFailureResponse("No user exists with id:" + id);
            failureResponse.put("userexists", userItem != null);
            return failureResponse;
        }
    }

}
