package com.chitter.controllers;

import com.chitter.model.TweetItem;
import com.chitter.services.TweetStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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

    @Autowired
    public ActionController(TweetStore tweetStore) {
        this.tweetStore = tweetStore;
    }


    @RequestMapping("tweet")
    @ResponseBody
    public TweetItem create(TweetItem tweetItem, HttpSession session) {
        return tweetStore.add(tweetItem);
    }


}
