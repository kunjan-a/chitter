package com.chitter.response;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 16/8/12
 * Time: 2:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class Response {
    public Map<Object, Object> getResponse(Object... objArr) {
        Assert.isTrue((objArr.length & 1) == 0);
        Map<Object, Object> response = new HashMap<Object, Object>();

        for (int i = 0; i < objArr.length; i++) {
            response.put(objArr[i], objArr[++i]);
        }
        return response;
    }

    public enum Success {
        FAILURE, SUCCESSFUL;


        @Override
        public String toString() {
            return String.valueOf(this.ordinal());
        }
    }

    public Map<Object, Object> getResponse(Success success, String msg) {
        return getResponse(success, msg, null);
    }

    public Map<Object, Object> getResponse(Success success, String msg, Object response) {
        return getResponse("Success", success.toString(), "msg", msg, "response", response);
    }

}
