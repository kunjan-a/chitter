package com.chitter.security.password;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 10/8/12
 * Time: 10:41 AM
 * To change this template use File | Settings | File Templates.
 */
public interface PasswordManager {
    public boolean verifyPassword(String passwdFromDb, String passwdToVerify, String email);

    public String getEncodedPasswd(String plainTextPasswd, String email);
}
