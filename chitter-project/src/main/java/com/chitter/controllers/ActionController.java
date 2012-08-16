package com.chitter.controllers;

import com.chitter.model.FeedItem;
import com.chitter.model.TweetItem;
import com.chitter.model.UserItem;
import com.chitter.response.FailureResponse;
import com.chitter.response.SuccessfulResponse;
import com.chitter.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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
    private static final SuccessfulResponse successfulResponse = new SuccessfulResponse();
    private static final FailureResponse failureResponse = new FailureResponse();
    private final FavouriteTweetStore favouriteTweetStore;
    private final FavouriteUserStore favouriteUserStore;

    @Autowired
    public ActionController(TweetStore tweetStore, UserStore userStore, FollowStore followStore, FavouriteTweetStore favouriteTweetStore, FavouriteUserStore favouriteUserStore) {
        this.tweetStore = tweetStore;
        this.userStore = userStore;
        this.followStore = followStore;
        this.favouriteTweetStore =  favouriteTweetStore;
        this.favouriteUserStore = favouriteUserStore;
    }


    @RequestMapping(value = "/uploadProfilePic", method = RequestMethod.POST)
    public ModelAndView
    handleFormUpload(@RequestParam("picFile") MultipartFile picFile, HttpSession session) throws IOException {
        UserItem useritem = userStore.getUserWithId((Long) session.getAttribute("userID"));
        ModelAndView mv = new ModelAndView("redirect:/edit/profile");
        if (!picFile.isEmpty()) {
            userStore.storeProfilePic(useritem, picFile);
            mv.addObject("imageUploadResponse", successfulResponse.getSuccessfulResponse("Image uploaded successfully."));
        } else {
            mv.addObject("imageUploadResponse", failureResponse.getFailureResponse("Oops!, there was an error in image upload. Please try again."));
        }
        return mv;
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object>
    changePassword(@RequestParam String currentPassword, @RequestParam String password,
                   @RequestParam String password2, HttpSession session) throws IOException {
       UserItem userItem = userStore.getUserWithCredentials(userStore.getUserWithId((Long) session.getAttribute("userID")), currentPassword);
        if (userItem != null) {
            userStore.updatePassword(password, userItem);
            return successfulResponse.getSuccessfulResponse("Password updated successfully.");
        } else {
            return failureResponse.getFailureResponse("Your current password did not match.");
        }
    }

    @RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object>
    updateProfile(@RequestParam(required = false) String name, @RequestParam(required = false) String location,
                  @RequestParam(required = false) String bio, @RequestParam(required = false) String website,
                  UserItem userItem, HttpSession session) throws IOException {
        userItem = userStore.updateProfileForCurrUser(userItem);
        final Map<Object, Object> response = successfulResponse.getSuccessfulResponse("You details have been updated successfully.");
        response.put("user",userItem);
        return response;
    }

    @RequestMapping("tweet")
    @ResponseBody
    public Map<Object, Object>
    tweet(@RequestParam String text, TweetItem tweetItem, HttpSession session) {
        Map<Object, Object> response = successfulResponse.getSuccessfulResponse("Tweet successful",tweetStore.addTweet(tweetItem));
        response.put("user", userStore.getUserWithId((Long) session.getAttribute("userID")));
        return response;
    }


    @RequestMapping("retweet")
    @ResponseBody
    public Map<Object, Object>
    retweet(@RequestParam long id, TweetItem tweetItem, HttpSession session) {
        Map<Object, Object> response;
        List<FeedItem> retweet = tweetStore.retweet(tweetItem);
        if (retweet != null)
            response = successfulResponse.getSuccessfulResponse("Retweet successful",retweet);
        else
            response = failureResponse.getFailureResponse("Invalid ReTweet Request");

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
                response = successfulResponse.getSuccessfulResponse("You will receive all new tweets from "+userItem.getName());
            else
                response = failureResponse.getFailureResponse("Sorry. Some error occurred. Please try again.");
        } else
            response = failureResponse.getFailureResponse("No user exists with id:" + String.valueOf(user_id));
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
            response = failureResponse.getFailureResponse("Sorry. Some error occurred. Please try again.");
        else
            response = successfulResponse.getSuccessfulResponse("You will not receive new tweets from "+userItem.getName());
        response.put("user", userStore.getUserWithId((Long) session.getAttribute("userID")));
        return response;
    }

    @RequestMapping("markFavouriteTweet")
    @ResponseBody
    public Map<Object, Object>
    markFavouriteTweet(@RequestParam long tweet_id,@RequestParam long tweet_creator_id, HttpSession session) {
        Map<Object, Object> response;
        TweetItem tweetItem = tweetStore.getTweetWithId(tweet_id, tweet_creator_id);
        if(tweetItem!=null){
            if(favouriteTweetStore.markFavouriteOfCurrent(tweetItem))
                response = successfulResponse.getSuccessfulResponse("Made favourite.");
            else
                response = failureResponse.getFailureResponse("Sorry. Some error occurred. Please try again.");
        } else
            response = failureResponse.getFailureResponse("Tweet not found.");
        response.put("user", userStore.getUserWithId((Long) session.getAttribute("userID")));
        return response;
    }


    @RequestMapping("unmarkFavouriteTweet")
    @ResponseBody
    public Map<Object, Object>
    unmarkFavouriteTweet(@RequestParam long tweet_id,@RequestParam long tweet_creator_id, HttpSession session) {
        Map<Object, Object> response;
        TweetItem tweetItem = tweetStore.getTweetWithId(tweet_id, tweet_creator_id);
        if (tweetItem != null && !favouriteTweetStore.unmarkFavouriteOfCurrent(tweetItem))
            response = failureResponse.getFailureResponse("Sorry. Some error occurred. Please try again.");
        else
            response = successfulResponse.getSuccessfulResponse();

        response.put("user", userStore.getUserWithId((Long) session.getAttribute("userID")));
        return response;
    }

    @RequestMapping("markFavouriteUser")
    @ResponseBody
    public Map<Object, Object>
    markFavouriteUser(@RequestParam long user_id, HttpSession session) {
        Map<Object, Object> response;
        UserItem userItem = userStore.getUserWithId(user_id);
        if (userItem != null) {
            if (favouriteUserStore.markFavouriteOfCurrent(userItem))
                response = successfulResponse.getSuccessfulResponse(userItem.getName()+" has been added to your favourite people.");
            else
                response = failureResponse.getFailureResponse("Sorry. Some error occurred. Please try again.");
        } else
            response = failureResponse.getFailureResponse("No user exists with id:" + String.valueOf(user_id));
        response.put("user", userStore.getUserWithId((Long) session.getAttribute("userID")));
        return response;
    }


    @RequestMapping("unmarkFavouriteUser")
    @ResponseBody
    public Map<Object, Object>
    unmarkFavouriteUser(@RequestParam long user_id, HttpSession session) {
        Map<Object, Object> response;
        UserItem userItem = userStore.getUserWithId(user_id);
        if (userItem != null && !favouriteUserStore.unmarkFavouriteOfCurrent(userItem))
            response = failureResponse.getFailureResponse("Sorry. Some error occurred. Please try again.");
        else
            response = successfulResponse.getSuccessfulResponse(userItem.getName() + " has been removed from your favourites.");

        response.put("user", userStore.getUserWithId((Long) session.getAttribute("userID")));
        return response;
    }


}
