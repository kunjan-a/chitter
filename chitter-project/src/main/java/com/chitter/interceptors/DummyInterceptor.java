package com.chitter.interceptors;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 23/7/12
 * Time: 8:18 PM
 * To change this template use File | Settings | File Templates.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DummyInterceptor extends HandlerInterceptorAdapter {
    private final ThreadLocal<Long> userID;        // We dont need to keep passing session object or user id at each place.

    @Autowired
    public DummyInterceptor(@Qualifier("userID") ThreadLocal<Long> userID) {
        this.userID = userID;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        return true;
    }
}
