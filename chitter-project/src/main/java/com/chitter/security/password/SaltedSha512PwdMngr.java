package com.chitter.security.password;


import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 10/8/12
 * Time: 10:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class SaltedSha512PwdMngr implements PasswordManager {
    private final String SERVER_SALT = "qfu71eubdUIY_*dfgS*)daf#";

    @Override
    public boolean verifyPassword(String passwdFromDb, String passwdToVerify, String email) {
        return getEncodedPasswd(passwdToVerify, email).equals(passwdFromDb);
    }

    @Override
    public String getEncodedPasswd(String plainTextPwd, String email) {
        String salt = getSalt(plainTextPwd, email);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] digest = new byte[0];
        if (md != null) {
            try {
                digest = md.digest((plainTextPwd + salt).getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return new BASE64Encoder().encode(digest);
    }

    private String getSalt(String plainTextPwd, String uniqueString) {
        int charCount = plainTextPwd.length() + SERVER_SALT.length() + uniqueString.length();
        String pickCharsFrom = plainTextPwd + SERVER_SALT + uniqueString;
        Random randomizer = new Random(charCount);
        StringBuilder salt = new StringBuilder();
        for (int i = 0; i < charCount; i++) {
            salt.append(pickCharsFrom.charAt(randomizer.nextInt(charCount)));
        }
        return salt.toString();
    }
}
