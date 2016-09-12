package com.github.mavogel.ilias.model;

import org.apache.commons.lang3.Validate;

/**
 * Created by mavogel on 8/30/16.
 * <p>
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
    }

    ;

    private final LOGIN_MODE loginMode;
    private final String endpoint, client, username, password;
    private final int maxFolderDepth;

    private LoginConfiguration(final LOGIN_MODE loginMode, final String endpoint, final String client,
                               final String username, final String password, final int maxFolderDepth) {
        Validate.notEmpty(endpoint, "endpoint is empty");
        Validate.notEmpty(client, "client is empty");
        Validate.notEmpty(username, "username is empty");
        Validate.notEmpty(password, "password is empty");
        this.loginMode = loginMode;
        this.endpoint = endpoint;
        this.client = client;
        this.username = username;
        this.password = password;
        this.maxFolderDepth = maxFolderDepth;
    }

    /**
     * Creates a login configuration with standard login
     *
     * @param endpoint       the endpoint of the ilias server
     * @param client         the client id
     * @param username       the username
     * @param password       the password
     * @param maxFolderDepth the maximum folder depth
     * @return the configuration
     */
    public static LoginConfiguration asStandardLogin(final String endpoint, final String client,
                                                     final String username, final String password, final int maxFolderDepth) {
        return new LoginConfiguration(LOGIN_MODE.STD, endpoint, client, username, password, maxFolderDepth);
    }

    /**
     * Creates a login configuration with LDAP login
     *
     * @param endpoint the endpoint of the ilias server
     * @param client   the client id
     * @param username the username
     * @param password the password
     * @param maxFolderDepth the maximum folder depth
     * @return the configuration
     */
    public static LoginConfiguration asLDAPLogin(final String endpoint, final String client,
                                                 final String username, final String password, final int maxFolderDepth) {
        return new LoginConfiguration(LOGIN_MODE.LDAP, endpoint, client, username, password, maxFolderDepth);
    }

    /**
     * Not yet supported
     *
     * @throws UnsupportedOperationException
     */
    public static LoginConfiguration asCASLogin() {
        throw new UnsupportedOperationException("login with CAS is not yet supported");
    }

    public LOGIN_MODE getLoginMode() {
        return loginMode;
    }

    public String getEndpoint() {
        return endpoint;
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

    public int getMaxFolderDepth() {
        return maxFolderDepth;
    }
}
