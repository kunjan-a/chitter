package com.chitter.controllers;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 23/7/12
 * Time: 8:18 PM
 * To change this template use File | Settings | File Templates.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Sets userID if logged in. Doesn't do any redirect and always returns true..
 */
public class AuthInterceptor extends BaseInterceptor {

    @Autowired
    public AuthInterceptor(@Qualifier("userID") ThreadLocal<Long> userID) {
        super(userID);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);
        if (session != null) {
            String userName = (String) session.getAttribute("userName");
            if (userName != null) {
                userID.set((Long) session.getAttribute("userID"));
                return true;
            }
        } else
            userID.set(null);

        return false;
    }
}
