package com.chitter.response;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 16/8/12
 * Time: 2:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class SuccessfulResponse extends Response {
    public Map<Object, Object> getSuccessfulResponse() {
        return getSuccessfulResponse("");
    }

    public Map<Object, Object> getSuccessfulResponse(String msg) {
        return getSuccessfulResponse(msg, null);
    }

    public Map<Object, Object> getSuccessfulResponse(Object response) {
        return getSuccessfulResponse("", response);
    }

    public Map<Object, Object> getSuccessfulResponse(String msg, Object response) {
        return getResponse(Success.SUCCESSFUL, msg, response);

    }
}