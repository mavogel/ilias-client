package com.github.mavogel.ilias.client;/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016 Manuel Vogel
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

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.utils.IliasUtils;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

/**
 * Establishes a connection to the ilias server for debugging purposes of the result xml.
 * <p>
 * Created by mavogel on 9/16/16.
 */
public class TestConnector {

    public static void main(String[] args) throws RemoteException {
        // == prepare
        String endpoint = "";
        String client = "";
        String username = "";
        String password = "";
        int maxFolderDepth = 5;
        LoginConfiguration loginConfiguration = LoginConfiguration.asLDAPLogin(endpoint, client, username, password, maxFolderDepth);
        ILIASSoapWebservicePortType wsEndpoint = null;
        UserDataIds userData = null;
        try {
            wsEndpoint = IliasUtils.createWsEndpoint(loginConfiguration);
            userData = IliasUtils.getUserData(loginConfiguration, wsEndpoint);

            // == go
            // TODO insert your action here
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            // == teardown
            if (userData != null && userData.getSid() != null) {
                wsEndpoint.logout(userData.getSid());
            }
        }
    }
}