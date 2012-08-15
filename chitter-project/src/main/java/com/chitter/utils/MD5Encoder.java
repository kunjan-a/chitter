package com.chitter.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 15/8/12
 * Time: 8:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class MD5Encoder {
    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    public String toMd5(String target) throws NoSuchAlgorithmException, UnsupportedEncodingException {
            return toMd5(target, "US-ASCII");
    }

    public String toMd5(String target, String charsetName) throws NoSuchAlgorithmException, UnsupportedEncodingException {
            final byte[] md5 = MessageDigest.getInstance("MD5").digest(
                    target.getBytes(charsetName));
            final char[] md5Chars = new char[32];
            int i = 0;
            for (final byte b : md5) {
                md5Chars[i++] = HEXDIGITS[(b >> 4) & 0xF];
                md5Chars[i++] = HEXDIGITS[b & 0xF];
            }
            return new String(md5Chars);
    }
}
