package com.github.mavogel.ilias.wrapper;/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2017 Manuel Vogel
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 *  https://opensource.org/licenses/MIT
 */

import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by mavogel on 6/26/17.
 */
public abstract class AbstractIliasEndpoint implements IliasEndpoint {

    protected LoginConfiguration loginConfiguration;
    protected UserDataIds userDataIds;

    /**
     * @return the user data ids
     */
    public UserDataIds getUserDataIds() {
        return userDataIds;
    }

    /**

     * Creates the connection to the endpoint and retrieves the user credentials
     *
     * @param loginConfiguration the login configuration data
     * @throws Exception in case the endpoint cannot be created or the login fails
     */
    protected AbstractIliasEndpoint(final LoginConfiguration loginConfiguration) throws Exception {
        this.loginConfiguration = loginConfiguration;
        this.createAndSetEndpoint();
        this.getAndSetUserData();
    }

    /**
     * Creates the connection to the endpoint.

     *
     * @throws Exception if the endpoint cannot be created
     */
    protected abstract void createAndSetEndpoint() throws Exception;

    /**
     * Gets and set the user data.
     *
     * @throws Exception the the user data is wrong
     */
    protected abstract void getAndSetUserData() throws Exception;

    /**
     * Converts a {@link LocalDate} into epoch seconds represented in the
     * time zone of the machine this tool is running. It's expected the ilias
     * server is running in the same time zone.
     *
     * @param localDateTime the local date time to convert
     * @return the seconds passed from the epoch
     */
    protected long toEpochSecond(final LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).toEpochSecond();
    }
}
