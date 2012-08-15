package com.chitter.controllers;

import com.chitter.model.FeedItem;
import com.chitter.model.TweetItem;
import com.chitter.model.UserItem;
import com.chitter.services.FollowStore;
import com.chitter.services.TweetStore;
import com.chitter.services.UserStore;
import com.chitter.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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


    @RequestMapping(value = "/uploadProfilePic", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView
    handleFormUpload(@RequestParam("picFile") MultipartFile picFile, HttpSession session, HttpServletResponse response) throws IOException {
        UserItem useritem = userStore.getUserWithId((Long) session.getAttribute("userID"));
        ModelAndView mv = new ModelAndView("redirect:/edit/profile");
        if (!picFile.isEmpty()) {
            userStore.storeProfilePic(useritem, picFile);
            mv.addObject("imageUploadResponse",ResponseUtil.getSuccessfulResponse("Image uploaded successfully."));
        } else {
            mv.addObject("imageUploadResponse",ResponseUtil.getFailureResponse("Oops!, there was an error in image upload. Please try again."));
        }
        return mv;
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object>
    changePassword(@RequestParam String currentPassword, @RequestParam String password,
                   @RequestParam String password2, HttpSession session) throws IOException {

        UserItem userItem = new UserItem();
        userItem.setId((Long) session.getAttribute("userID"));
        userItem = userStore.getUserWithCredentials(userItem, currentPassword);

        if (userItem != null) {
            userStore.updatePassword(password, userItem);
            return ResponseUtil.getSuccessfulResponse("Password updated successfully.");
        } else {
            return ResponseUtil.getFailureResponse("Your current password did not match.");
        }
    }

    @RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object>
    updateProfile(@RequestParam(required = false) String name, @RequestParam(required = false) String location,
                  @RequestParam(required = false) String bio, @RequestParam(required = false) String website,
                  UserItem userItem, HttpSession session) throws IOException {
        userItem = userStore.updateProfileForCurrUser(userItem);
        final Map<Object, Object> successfulResponse = ResponseUtil.getSuccessfulResponse("User details updated successfully.");
        successfulResponse.put("user",userItem);
        return successfulResponse;
    }

    @RequestMapping("tweet")
    @ResponseBody
    public Map<Object, Object>
    tweet(@RequestParam String text, TweetItem tweetItem, HttpSession session) {
        Map<Object, Object> successfulResponse = ResponseUtil.getSuccessfulResponse(tweetStore.addTweet(tweetItem));
        successfulResponse.put("user", userStore.getUserWithId((Long) session.getAttribute("userID")));
        return successfulResponse;
    }


    @RequestMapping("retweet")
    @ResponseBody
    public Map<Object, Object>
    retweet(@RequestParam long id, TweetItem tweetItem, HttpSession session) {
        Map<Object, Object> response;
        List<FeedItem> retweet = tweetStore.retweet(tweetItem);
        if (retweet != null)
            response = ResponseUtil.getSuccessfulResponse(retweet);
        else
            response = ResponseUtil.getFailureResponse("Invalid ReTweet Request");

        response.put("user", userStore.getUserWithId((Long) session.getAttribute("userID")));
        return response;
    }


    @RequestMapping("follow")
    @ResponseBody
    public Map<Object, Object>
    follow(@RequestParam long user_id, HttpSession session) {
        Map<Object, Object> response;
        UserItem userItem = userStore.getUserWithId(user_id);
        if (userItem != null) {
            if (followStore.follow(userItem))
                response = ResponseUtil.getSuccessfulResponse();
            else
                response = ResponseUtil.getFailureResponse("Sorry. Some error occurred. Please try again.");
        } else
            response = ResponseUtil.getFailureResponse("No user exists with id:" + String.valueOf(user_id));
        response.put("user", userStore.getUserWithId((Long) session.getAttribute("userID")));
        return response;
    }


    @RequestMapping("unfollow")
    @ResponseBody
    public Map<Object, Object>
    unfollow(@RequestParam long user_id, HttpSession session) {
        Map<Object, Object> response;
        UserItem userItem = userStore.getUserWithId(user_id);
        if (userItem != null && !followStore.unfollow(userItem))
            response = ResponseUtil.getFailureResponse("Sorry. Some error occurred. Please try again.");
        else
            response = ResponseUtil.getSuccessfulResponse();
        response.put("user", userStore.getUserWithId((Long) session.getAttribute("userID")));
        return response;
    }

}
