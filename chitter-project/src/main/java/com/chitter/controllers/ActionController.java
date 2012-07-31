package com.chitter.controllers;

import com.chitter.model.TweetItem;
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
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 27/7/12
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("action")
public class ActionController {
    private final TweetStore tweetStore;
    private final UserStore userStore;
    private final FollowStore followStore;

    @Autowired
    public ActionController(TweetStore tweetStore, UserStore userStore, FollowStore followStore) {
        this.tweetStore = tweetStore;
        this.userStore = userStore;
        this.followStore = followStore;
    }


    @RequestMapping("tweet")
    @ResponseBody
    public Map<Object, Object> create(@RequestParam String text, TweetItem tweetItem, HttpSession session) {
        return ResponseUtil.getSuccessfulResponse(tweetStore.add(tweetItem));
    }

    @RequestMapping("follow")
    @ResponseBody
    public Map<Object, Object> follow(@RequestParam long user_id, HttpSession session) {
        UserItem userItem = userStore.getUserWithId(user_id);
        if (userItem != null) {
            if (followStore.follow(userItem))
                return ResponseUtil.getSuccessfulResponse();
            else
                return ResponseUtil.getFailureResponse("Sorry. Some error occurred. Please try again.");
        } else
            return ResponseUtil.getFailureResponse("No user exists with id:" + String.valueOf(user_id));
    }

    @RequestMapping("unfollow")
    @ResponseBody
    public Map<Object, Object> unfollow(@RequestParam long user_id, HttpSession session) {
        UserItem userItem = userStore.getUserWithId(user_id);
        if (userItem != null && !followStore.unfollow(userItem))
            return ResponseUtil.getFailureResponse("Sorry. Some error occurred. Please try again.");

        return ResponseUtil.getSuccessfulResponse();
    }

    @RequestMapping("fetchTweets/{id}")
    @ResponseBody
    public Map<Object, Object> fetchTweets(@PathVariable long id, HttpSession session) {
        UserItem userItem = userStore.getUserWithId(id);
        if (userItem != null) {
            Map<Object, Object> response = ResponseUtil.getSuccessfulResponse(tweetStore.fetchTweetsBy(id));
            response.put("user", userItem);
            return response;
        } else
            return ResponseUtil.getFailureResponse("No user exists with id:" + id);
    }

}
