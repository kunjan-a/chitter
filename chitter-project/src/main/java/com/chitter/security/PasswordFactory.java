package com.chitter.security;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 10/8/12
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class PasswordFactory {
    public final int CURRENT_SCHEME = 1;

    public PasswordManager getPasswordManager(int scheme) {
        switch (scheme) {
            case 0:
                return new PlainTextPwdMngr();
            case 1:
                return new SaltedSha512PwdMngr();
        }
        return null;
    }
}
