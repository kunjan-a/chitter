package com.chitter.controllers;

import com.chitter.model.FeedItem;
import com.chitter.model.UserItem;
import com.chitter.model.UserSearchResultItem;
import com.chitter.response.FailureResponse;
import com.chitter.response.SuccessfulResponse;
import com.chitter.services.*;
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
    private final FavouriteTweetStore favouriteTweetStore;
    private final FavouriteUserStore favouriteUserStore;

    private static final SuccessfulResponse successfulResponse = new SuccessfulResponse();
    private static final FailureResponse failureResponse = new FailureResponse();

    @Autowired
    public RestController(TweetStore tweetStore, UserStore userStore, FollowStore followStore, FavouriteTweetStore favouriteTweetStore, FavouriteUserStore favouriteUserStore) {
        this.tweetStore = tweetStore;
        this.userStore = userStore;
        this.followStore = followStore;
        this.favouriteTweetStore =  favouriteTweetStore;
        this.favouriteUserStore = favouriteUserStore;
    }


    @RequestMapping("{userId}/tweets")
    @ResponseBody
    public Map<Object, Object>
    fetchTweets(@PathVariable long userId, @RequestParam(required = false) Integer numResults, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listTweets(userItem, numResults);
            response = getFeedsResponse(userItem, feeds);
        } else {
            response = userIdNotFoundFailureResponse(userId);
        }
        return responseWithNecessaryFooters(session, response, validId);
    }

    @RequestMapping("{userId}/tweetsBefore")
    @ResponseBody
    public Map<Object, Object>
    fetchTweetsBefore(@PathVariable long userId, @RequestParam(required = false) Integer numResults, @RequestParam(required = false) Timestamp beforeTime, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listTweetsBefore(userItem, numResults, beforeTime);
            response = getFeedsResponse(userItem, feeds);
        } else {
            response = userIdNotFoundFailureResponse(userId);
        }
        return responseWithNecessaryFooters(session, response, validId);
    }

    @RequestMapping("{userId}/tweetsAfter")
    @ResponseBody
    public Map<Object, Object>
    fetchTweetsAfter(@PathVariable long userId, @RequestParam(required = false) Integer numResults, @RequestParam(required = false) Timestamp afterTime, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listTweetsAfter(userItem, numResults, afterTime);
            response = getFeedsResponse(userItem, feeds);
        } else {
            response = userIdNotFoundFailureResponse(userId);
        }
        return responseWithNecessaryFooters(session, response, validId);
    }

    @RequestMapping("{userId}/followers")
    @ResponseBody
    public Map<Object, Object>
    followers(@PathVariable long userId, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<UserItem> followers = followStore.listFollowers(userItem);
            response = successfulResponse.getSuccessfulResponse(followers);
            getUsersResponse(response, userItem, followers);
        } else {
            response = userIdNotFoundFailureResponse(userId);
        }
        return responseWithNecessaryFooters(session, response, validId);
    }

    @RequestMapping("{userId}/following")
    @ResponseBody
    public Map<Object, Object>
    following(@PathVariable long userId, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<UserItem> followed = followStore.listFollowed(userItem);
            response = successfulResponse.getSuccessfulResponse(followed);
            getUsersResponse(response, userItem, followed);
        } else {
            response = userIdNotFoundFailureResponse(userId);
        }
        return responseWithNecessaryFooters(session, response, validId);
    }

    @RequestMapping("{userId}/feedsAfter")
    @ResponseBody
    public Map<Object, Object>
    feedsAfter(@PathVariable long userId, @RequestParam(required = false) Integer numResults, @RequestParam(required = false) Timestamp afterTime, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listFeedsAfter(userItem, numResults, afterTime);
            response = getFeedsResponse(userItem, feeds);
        } else {
            response = userIdNotFoundFailureResponse(userId);
        }
        return responseWithNecessaryFooters(session, response, validId);
    }

    @RequestMapping("{userId}/feedsBefore")
    @ResponseBody
    public Map<Object, Object>
    feedsBefore(@PathVariable long userId, @RequestParam(required = false) Integer numResults, @RequestParam(required = false) Timestamp beforeTime, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listFeedsBefore(userItem, numResults, beforeTime);
            response = getFeedsResponse(userItem, feeds);
        } else {
            response = userIdNotFoundFailureResponse(userId);
        }
        return responseWithNecessaryFooters(session, response, validId);
    }

    @RequestMapping("{userId}/feeds")
    @ResponseBody
    public Map<Object, Object>
    feeds(@PathVariable long userId, @RequestParam(required = false) Integer numResults, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = tweetStore.listFeeds(userItem, numResults);
            response = getFeedsResponse(userItem, feeds);
        } else {
            response = userIdNotFoundFailureResponse(userId);
        }
        return responseWithNecessaryFooters(session, response, validId);
    }

    @RequestMapping("/tweet/{tweetId}")
    @ResponseBody
    public Map<Object, Object>
    getFeedItemForTweet(@PathVariable long tweetId, @RequestParam Long userId, HttpSession session) {
        List<FeedItem> feeds = tweetStore.getFeedsForTweet(tweetId, userId);
        if (feeds != null) {
            Map<Object, Object> response = successfulResponse.getSuccessfulResponse(feeds);
            response.put("users", userStore.getUserItems(feeds));
            response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
            return response;
        } else {
            return failureResponse.getFailureResponse("No tweet exists with the id:" + tweetId);
        }
    }

    @RequestMapping("{userId}/favouriteUsers")
    @ResponseBody
    public Map<Object, Object>
    favouriteUsers(@PathVariable long userId, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<UserItem> favourites = favouriteUserStore.listFavourites(userItem);
            response = successfulResponse.getSuccessfulResponse(favourites);
            getUsersResponse(response, userItem, favourites);
        } else {
            response = userIdNotFoundFailureResponse(userId);
        }
        return responseWithNecessaryFooters(session, response, validId);
    }

    @RequestMapping("{userId}/favouriteTweets")
    @ResponseBody
    public Map<Object, Object>
    favouriteTweets(@PathVariable long userId, HttpSession session) {
        Map<Object, Object> response;

        UserItem userItem = userStore.getUserWithId(userId);
        final boolean validId = userItem != null;
        if (validId) {
            List<FeedItem> feeds = favouriteTweetStore.listFavourites(userItem);
            response = getFeedsResponse(userItem, feeds);
        } else {
            response = userIdNotFoundFailureResponse(userId);
        }
        return responseWithNecessaryFooters(session, response, validId);
    }

    private Map<Object, Object> getFeedsResponse(UserItem userItem, List<FeedItem> feeds) {
        Map<Object, Object> response;
        response = successfulResponse.getSuccessfulResponse(feeds);
        response.put("users", userStore.getUserItems(feeds));

        response.put("user", userItem);
        response.put("favourite", favouriteUserStore.isCurrentsFavourite(userItem));
        response.put("follows", followStore.currentFollows(userItem));
        response.put("retweeted", tweetStore.retweetedByCurrent(feeds));
        response.put("favourite", favouriteTweetStore.favouritesOfCurrent(feeds));
        return response;
    }


    private void getUsersResponse(Map<Object, Object> response, UserItem userItem, List<UserItem> followers) {
        response.put("user", userItem);
        response.put("favourite", favouriteUserStore.isCurrentsFavourite(userItem));
        response.put("usersFollowed", followStore.currentFollows(followers));
        response.put("follows", followStore.currentFollows(userItem));
        response.put("favourites", favouriteUserStore.favouritesOfCurrent(followers));
    }

    private Map<Object, Object> responseWithNecessaryFooters(HttpSession session, Map<Object, Object> response, boolean validId) {
        response.put("userexists", validId);
        addCurrentUserItem(session, response);
        return response;
    }

    private Map<Object, Object> userIdNotFoundFailureResponse(long userId) {
        return failureResponse.getFailureResponse("No user exists with id:" + userId);
    }

    private void addCurrentUserItem(HttpSession session, Map<Object, Object> response) {
        final Long currUserId = (Long) session.getAttribute("userID");
        UserItem currUserItem = null;
        if (currUserId != null) {
            currUserItem = userStore.getUserWithId(currUserId);
        }
        response.put("currUserItem", currUserItem);
    }

    @RequestMapping(value = "/search/users")
    @ResponseBody
    public List<UserSearchResultItem>
    searchUsers(@RequestParam String term, @RequestParam(required = false) Integer numResults) {
        return userStore.getMatchingUsers(term, numResults);
    }

}
