package com.chitter.security;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 10/8/12
 * Time: 10:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlainTextPwdMngr implements PasswordManager {

    @Override
    public boolean verifyPassword(String passwdFromDb, String passwdToVerify, String email) {
        return passwdToVerify.equals(passwdFromDb);
    }

    @Override
    public String getEncodedPasswd(String plainTextPwd, String email) {
        return plainTextPwd;
    }
}
