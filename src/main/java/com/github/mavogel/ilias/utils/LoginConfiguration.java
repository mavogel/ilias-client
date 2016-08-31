package com.github.mavogel.ilias.utils;

import org.apache.commons.lang3.Validate;

/**
 * Created by mavogel on 8/30/16.
 *
 * The configuration for the login.
 */
public class LoginConfiguration {

    public enum LOGIN_MODE {
        /**
         * The standard login: {@link com.github.mavogel.client.ILIASSoapWebservicePortType#login(String, String, String)}
         */
        STD,

        /**
         * CAS as login mode: {@link com.github.mavogel.client.ILIASSoapWebservicePortType#loginCAS(String, String, String)}
         */
        CAS,

        /**
         * LDAP as login mode: {@link com.github.mavogel.client.ILIASSoapWebservicePortType#loginLDAP(String, String, String)}
         */
        LDAP
    };

    private final LOGIN_MODE loginMode;
    private final String client, username, password;

    private LoginConfiguration(final LOGIN_MODE loginMode,
                               final String client,
                               final String username,
                               final String password) {
        Validate.notEmpty(client, "client is empty");
        Validate.notEmpty(username,"username is empty");
        Validate.notEmpty(password, "password is empty");
        this.loginMode = loginMode;
        this.client = client;
        this.username = username;
        this.password = password;
    }

    /**
     * Creates a login configuration with standard login
     *
     * @param client the client id
     * @param username the username
     * @param password the password
     * @return the configuration
     */
    public static LoginConfiguration asStandardLogin(final String client, final String username, final String password) {
        return new LoginConfiguration(LOGIN_MODE.STD, client, username, password);
    }

    /**
     * Creates a login configuration with LDAP login
     *
     * @param client the client id
     * @param username the username
     * @param password the password
     * @return the configuration
     */
    public static LoginConfiguration asLDAPLogin(final String client, final String username, final String password) {
        return new LoginConfiguration(LOGIN_MODE.LDAP, client, username, password);
    }

    /**
     * Not yet supported
     * @throws UnsupportedOperationException
     */
    public static LoginConfiguration asCASLogin() {
        throw new UnsupportedOperationException("login with CAS is not yet supported");
    }

    public LOGIN_MODE getLoginMode() {
        return loginMode;
    }

    public String getClient() {
        return client;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
