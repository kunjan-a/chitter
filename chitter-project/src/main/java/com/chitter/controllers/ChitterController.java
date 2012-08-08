package com.chitter.controllers;

import com.chitter.model.FeedItem;
import com.chitter.model.FollowItem;
import com.chitter.model.UserItem;
import com.chitter.services.FollowStore;
import com.chitter.services.TweetStore;
import com.chitter.services.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: praveen
 * Date: 31/7/12
 * Time: 7:04 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class ChitterController {
    private final TweetStore tweetStore;
    private final UserStore userStore;
    private final FollowStore followStore;


    @Autowired
    public ChitterController(TweetStore tweetStore, UserStore userStore, FollowStore followStore) {
        this.tweetStore = tweetStore;
        this.userStore = userStore;
        this.followStore = followStore;
    }

    @RequestMapping("/")
    public ModelAndView index(HttpSession session) {
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("name", session.getAttribute("userName"));
        UserItem userItem = userStore.getUserWithId((Long) session.getAttribute("userID"));
        List<FeedItem> feeds = tweetStore.listFeeds(userItem);
        mv.addObject("feeds", feeds);

        mv.addObject("users", userStore.getUserItems(feeds));
        mv.addObject("user", userItem);
        return mv;
    }

    @RequestMapping(value = "/user/{id}")
    public ModelAndView profileGet(@PathVariable long id, HttpSession session) {
        ModelAndView mv = new ModelAndView("profile");
        UserItem userItem = userStore.getUserWithId(id);
        mv.addObject("userexists", userItem != null);
        if (userItem == null) {
            userItem = new UserItem();
            userItem.setId(id);
        } else {
            List<FeedItem> feeds = tweetStore.listTweets(userItem);
            mv.addObject(feeds);

            mv.addObject("users", userStore.getUserItems(feeds));
            mv.addObject("follows", followStore.currentFollows(userItem));
        }

        mv.addObject("user", userItem);

        return mv;
    }

    @RequestMapping("/user/{id}/followers")
    public ModelAndView followers(@PathVariable long id, HttpSession session) {
        ModelAndView mv = new ModelAndView("followers");
        UserItem userItem = userStore.getUserWithId(id);
        mv.addObject("userexists", userItem != null);
        if (userItem == null) {
            userItem = new UserItem();
            userItem.setId(id);
        } else {
            List<UserItem> followers = followStore.listFollowers(userItem);
            mv.addObject("followers", followers);

            List<FollowItem> follows = followStore.currentFollows(followers);
            mv.addObject("follows", follows);
        }
        mv.addObject("user", userItem);

        return mv;

    }

    @RequestMapping("/user/{id}/following")
    public ModelAndView following(@PathVariable long id, HttpSession session) {
        ModelAndView mv = new ModelAndView("following");
        UserItem userItem = userStore.getUserWithId(id);
        mv.addObject("userexists", userItem != null);
        if (userItem == null) {
            userItem = new UserItem();
            userItem.setId(id);
        } else {
            List<UserItem> followed = followStore.listFollowed(userItem);
            mv.addObject("following", followed);

            List<FollowItem> follows = followStore.currentFollows(followed);
            mv.addObject("follows", follows);
        }
        mv.addObject("user", userItem);

        return mv;

    }


}
