package com.chitter.utils;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 31/7/12
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ResponseUtil {

    public static Map<Object, Object> getResponse(Object... objArr) {
        Assert.isTrue((objArr.length & 1) == 0);
        Map<Object, Object> response = new HashMap<Object, Object>();

        for (int i = 0; i < objArr.length; i++) {
            response.put(objArr[i], objArr[++i]);
        }
        return response;
    }

    public static Map<Object, Object> getSuccessfulResponse(String msg) {
        return getSuccessfulResponse(msg, null);
    }

    public static Map<Object, Object> getSuccessfulResponse(Object response) {
        return getSuccessfulResponse("", response);
    }

    public static Map<Object, Object> getSuccessfulResponse(String msg, Object response) {
        return getResponse(Success.SUCCESSFUL, msg, response);
    }

    public static Map<Object, Object> getFailureResponse(String msg) {
        return getSuccessfulResponse(msg, null);
    }

    public static Map<Object, Object> getFailureResponse(Object response) {
        return getSuccessfulResponse("", response);
    }

    public static Map<Object, Object> getFailureResponse(String msg, Object response) {
        return getResponse(Success.FAILURE, msg, response);
    }

    public static Map<Object, Object> getSuccessfulResponse() {
        return getSuccessfulResponse("");
    }

    public enum Success {
        FAILURE, SUCCESSFUL;


        @Override
        public String toString() {
            return String.valueOf(this.ordinal());
        }
    }

    private static Map<Object, Object> getResponse(Success success, String msg) {
        return getResponse(success, msg, null);
    }

    private static Map<Object, Object> getResponse(Success success, String msg, Object response) {
        return getResponse("Success", success.toString(), "msg", msg, "response", response);
    }

}
