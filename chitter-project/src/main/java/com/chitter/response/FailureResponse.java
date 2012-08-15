package com.chitter.response;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 16/8/12
 * Time: 2:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class FailureResponse extends Response {
    public Map<Object, Object> getFailureResponse(String msg) {
        return getFailureResponse(msg, null);
    }

    public Map<Object, Object> getFailureResponse(Object response) {
        return getFailureResponse("", response);
    }

    public Map<Object, Object> getFailureResponse(String msg, Object response) {
        return getResponse(Success.FAILURE, msg, response);
    }

}
