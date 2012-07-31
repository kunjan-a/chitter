package com.chitter.controllers;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 23/7/12
 * Time: 8:22 PM
 * To change this template use File | Settings | File Templates.
 */

import com.chitter.model.UserItem;
import com.chitter.services.UserStore;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class UserController {
    private final UserStore userStore;

    @Autowired
    public UserController(UserStore userStore) {
        this.userStore = userStore;
    }


    @RequestMapping("/")
    public ModelAndView index(HttpSession session) {
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("name", session.getAttribute("userName"));
        return mv;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm() {
        return "login";
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) {
        signOut(session);
        return "redirect:/login";
    }

    @RequestMapping(value = "/request/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(UserItem userItem, HttpSession session) {
        String msg, success;
        userItem = userStore.getUserWithCredentials(userItem);
        if (userItem != null) {
            success = "1";
            msg = "Login successful";
            signIn(userItem, session);

        } else {
            msg = "Invalid username/password.";
            success = "0";
        }
        return ImmutableMap.of("Success", success, "msg", msg);
    }


    @RequestMapping(value = "/request/register", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> register(UserItem userItem, HttpSession session) {

        String msg, success;

        UserItem addedUserItem = userStore.add(userItem);
        if (addedUserItem != null) {
            success = "1";
            msg = "Registration successful. We trust you so no need to validate the email";
            signIn(addedUserItem, session);
        } else {
            msg = "Registration Failed.";
            success = "0";
            if (userStore.getUserWithEmail(userItem) != null)
                msg += " " + userItem.getEmail() + " is already registered.";
        }

        return ImmutableMap.of("Success", success, "msg", msg);
    }


    @RequestMapping(value = "/request/emailExists", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> emailExists(UserItem userItem, HttpSession session) {
        String isPresent;
        isPresent = userStore.getUserWithEmail(userItem) != null ? "1" : "0";
        return ImmutableMap.of("Exists", isPresent);
    }

    private void signOut(HttpSession session) {
        session.invalidate();
    }

    private void signIn(UserItem userItem, HttpSession session) {
        session.setAttribute("userName", userItem.getName());
        session.setAttribute("userID", userItem.getId());
    }


}