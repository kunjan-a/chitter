package com.chitter.controllers;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 23/7/12
 * Time: 8:22 PM
 * To change this template use File | Settings | File Templates.
 */

import com.chitter.model.UserItem;
import com.chitter.response.FailureResponse;
import com.chitter.response.SuccessfulResponse;
import com.chitter.services.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Controller
public class AuthController {
    private final UserStore userStore;
    private static final SuccessfulResponse successfulResponse = new SuccessfulResponse();
    private static final FailureResponse failureResponse = new FailureResponse();


    @Autowired
    public AuthController(UserStore userStore) {
        this.userStore = userStore;
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String
    loginForm() {
        return "login";
    }

    @RequestMapping(value = "/recoverAccount")
    @ResponseBody
    public Map<Object, Object>
    recoverAccount(@RequestParam String email, UserItem userItem) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        UserItem requestingUser = userStore.getUserWithEmail(userItem);
        try {
            if (requestingUser != null && !userStore.sendRecoveryInfo(requestingUser))
                return failureResponse.getFailureResponse("Some error occurred while sending recovery information to your email id.");
        } catch (MessagingException e) {
            return failureResponse.getFailureResponse("Some error occurred while sending recovery information to your email id.");
        }

        return successfulResponse.getSuccessfulResponse("Recovery instructions have been mailed to " + email);
    }

    @RequestMapping(value = "/forgotPassword")
    public String
    forgotPassword() {
        return "forgotPassword";
    }

    @RequestMapping(value = "/accountRecovery/{recoveryToken}", method = RequestMethod.GET)
    public ModelAndView
    recoverFromToken(@PathVariable String recoveryToken, HttpSession session) {
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
    public Map<Object, Object>
    resetPasswordByRecovery(@RequestParam String password, @RequestParam String password2, HttpSession session) {
        Map<Object, Object> response;
        UserItem userItem = (UserItem) session.getAttribute("recoveryUserItem");
        if (userItem != null) {
            userStore.updatePassword(password, userItem);
            response = successfulResponse.getSuccessfulResponse("Password updated successfully.");
        } else
            response = failureResponse.getFailureResponse("Invalid session. Please make a fresh request for password recovery.");

        return response;
    }

    @RequestMapping(value = "/logout")
    public String
    logout(HttpSession session) {
        signOut(session);
        return "redirect:/login";
    }

    @RequestMapping(value = "/request/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object>
    login(@RequestParam String email, @RequestParam String password, UserItem userItem, HttpSession session) {
        userItem = userStore.getUserWithCredentials(userItem, password);

        if (userItem != null) {
            signIn(userItem, session);
            return successfulResponse.getSuccessfulResponse("Login successful");

        } else
            return failureResponse.getFailureResponse("Invalid username/password.");
    }

    @RequestMapping(value = "/request/register", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object>
    register(@RequestParam String name, @RequestParam String email, @RequestParam String password, UserItem userItem, HttpSession session) throws IOException, NoSuchAlgorithmException, MessagingException {
        Map<Object, Object> response;

        UserItem addedUserItem = userStore.add(userItem, password);

        if (addedUserItem != null) {
            response = successfulResponse.getSuccessfulResponse("Registration successful. Please check the email sent to your email id for the last step.");
        } else {
            String msg = "This is embarassing. Registration Failed. Please try again.";
            response = failureResponse.getFailureResponse(msg);
        }

        return response;
    }


    private void signIn(UserItem userItem, HttpSession session) {
        session.setAttribute("userName", userItem.getName());
        session.setAttribute("userID", userItem.getId());
    }

    private void signOut(HttpSession session) {
        session.invalidate();
    }


}