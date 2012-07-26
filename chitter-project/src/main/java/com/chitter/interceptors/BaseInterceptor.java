package com.chitter.interceptors;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 23/7/12
 * Time: 8:18 PM
 * To change this template use File | Settings | File Templates.
 */

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * Sets userID if logged in. Doesn't do any redirect and always returns true..
 */
public class BaseInterceptor extends HandlerInterceptorAdapter {
    protected final ThreadLocal<Long> userID;        // We dont need to keep passing session object or user id at each place.


    public BaseInterceptor(ThreadLocal<Long> userID) {
        this.userID = userID;
    }

    protected boolean isLoggedIn() {
        return userID.get() != null;
    }

}
