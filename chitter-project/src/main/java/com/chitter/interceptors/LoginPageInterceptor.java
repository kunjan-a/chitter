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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Not Logged in : return true.<br/>
 * Logged in: redirect to "/" and return false
 */
public class LoginPageInterceptor extends BaseInterceptor {

    @Autowired
    public LoginPageInterceptor(@Qualifier("userID") ThreadLocal<Long> userID) {
        super(userID);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (isLoggedIn()) {
            response.sendRedirect("/");
            return false;
        }
        return true;

    }
}
