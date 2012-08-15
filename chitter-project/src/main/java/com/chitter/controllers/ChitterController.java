package com.chitter.controllers;

import com.chitter.services.FollowStore;
import com.chitter.services.TweetStore;
import com.chitter.services.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

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
    public String index(HttpSession session) {
        return "index";
    }

    @RequestMapping("/edit/profile")
    public ModelAndView editProfile(HttpSession session) {
        ModelAndView mv = new ModelAndView("editprofile");
        mv.addObject("user",userStore.getUserWithId((Long) session.getAttribute("userID")));
        return mv;
    }

}
