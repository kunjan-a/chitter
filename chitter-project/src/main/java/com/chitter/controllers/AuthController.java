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
import com.chitter.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Controller
public class AuthController {
    private final UserStore userStore;

    @Autowired
    public AuthController(UserStore userStore) {
        this.userStore = userStore;
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm() {
        return "login";
    }

    @RequestMapping(value = "/recoverAccount")
    @ResponseBody
    public Map<Object, Object> loginForm(@RequestParam String email, UserItem userItem) {
        UserItem requestingUser = userStore.getUserWithEmail(userItem);
        if (requestingUser != null && !userStore.sendRecoveryInfo(requestingUser))
            return ResponseUtil.getFailureResponse("Some error occurred while sending recovery information to your email id.");

        return ResponseUtil.getSuccessfulResponse("Recovery instructions have been mailed to " + email);
    }

    @RequestMapping(value = "/forgotPassword")
    public String forgotPassword() {
        return "forgotPassword";
    }

    @RequestMapping(value = "/accountRecovery/{recoveryToken}", method = RequestMethod.GET)
    public ModelAndView loginForm(@PathVariable String recoveryToken, HttpSession session) {
        ModelAndView mv = new ModelAndView("recoverPassword");
        UserItem userItem = userStore.validateAndExpireToken(recoveryToken);
        if (userItem != null) {
            session.setAttribute("recoveryUserItem", userItem);
            mv.addObject("valid", true);
        } else
            mv.addObject("valid", false);
        return mv;
    }

    @RequestMapping(value = "/resetPasswordByRecovery", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object> resetPassword(@RequestParam String password, @RequestParam String password2, HttpSession session) {
        Map<Object, Object> response;
        UserItem userItem = (UserItem) session.getAttribute("recoveryUserItem");
        if (userItem != null) {
            userStore.updatePassword(password, userItem);
            response = ResponseUtil.getSuccessfulResponse("Password updated successfully.");
        } else
            response = ResponseUtil.getFailureResponse("Invalid session. Please make a fresh request for password recovery.");

        return response;
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) {
        signOut(session);
        return "redirect:/login";
    }

    @RequestMapping(value = "/request/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object> login(@RequestParam String email, @RequestParam String password, UserItem userItem, HttpSession session) {
        userItem = userStore.getUserWithCredentials(userItem, password);

        if (userItem != null) {
            signIn(userItem, session);
            return ResponseUtil.getSuccessfulResponse("Login successful");

        } else
            return ResponseUtil.getFailureResponse("Invalid username/password.");
    }


    @RequestMapping(value = "/request/register", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object> register(@RequestParam String name, @RequestParam String email, @RequestParam String password, UserItem userItem, HttpSession session) throws IOException {
        Map<Object, Object> response;

        UserItem addedUserItem = userStore.add(userItem, password);

        if (addedUserItem != null) {
            response = ResponseUtil.getSuccessfulResponse("Registration successful. We trust you so no need to validate the email");
            signIn(addedUserItem, session);
        } else {
            String msg = "Registration Failed.";
            if (userStore.getUserWithEmail(userItem) != null)
                msg += " " + userItem.getEmail() + " is already registered.";
            response = ResponseUtil.getFailureResponse(msg);
        }

        return response;
    }


    @RequestMapping(value = "/request/emailExists", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object> emailExists(@RequestParam String email, UserItem userItem, HttpSession session) {
        String isPresent;
        isPresent = userStore.getUserWithEmail(userItem) != null ? "1" : "0";
        return ResponseUtil.getResponse("Exists", isPresent);
    }

    private void signIn(UserItem userItem, HttpSession session) {
        session.setAttribute("userName", userItem.getName());
        session.setAttribute("userID", userItem.getId());
    }

    private void signOut(HttpSession session) {
        session.invalidate();
    }


}