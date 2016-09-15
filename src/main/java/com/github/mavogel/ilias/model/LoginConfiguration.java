/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016 Manuel Vogel
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies
 *  or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 *  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  https://opensource.org/licenses/MIT
 */
package com.github.mavogel.ilias.model;

import org.apache.commons.lang3.Validate;

/**
 * The configuration for the login.
 *
 * Created by mavogel on 8/30/16.
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
