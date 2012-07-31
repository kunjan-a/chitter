package com.chitter.controllers;

import com.chitter.model.TweetItem;
import com.chitter.model.UserItem;
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

    @Autowired
    public ActionController(TweetStore tweetStore, UserStore userStore) {
        this.tweetStore = tweetStore;
        this.userStore = userStore;
    }


    @RequestMapping("tweet")
    @ResponseBody
    public Map<Object, Object> create(@RequestParam String text, TweetItem tweetItem, HttpSession session) {
        return ResponseUtil.getSuccessfulResponse(tweetStore.add(tweetItem));
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
