package com.chitter.controllers;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 23/7/12
 * Time: 8:18 PM
 * To change this template use File | Settings | File Templates.
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Not Logged in: redirect to "/login" and return false.<br/>
 * Logged in : return true
 */
public class LoggedInActionInterceptor extends BaseInterceptor {

    public LoggedInActionInterceptor(ThreadLocal<Long> userID) {
        super(userID);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (isLoggedIn()) {
            return true;
        }

        response.sendRedirect("/login");
        return false;

    }
}
