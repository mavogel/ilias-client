/*
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
package com.github.mavogel.ilias;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.ConfigurationsUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;

/**
 * Created by mavogel on 8/29/16.
 */
public class Starter {

    private static Logger LOG = Logger.getLogger(Starter.class);

    public static void main(String[] args) {
        Validate.notNull(args, "No arguments given");
        Validate.isTrue(args.length == 1, "Only one argument is allowed. The 'config.properties'");
        Validate.isTrue(!args[0].isEmpty(), "The argument is empty");
        createEndpointAndRun(ConfigurationsUtils.createLoginConfiguration(args[0]));
    }

    /**
     * Starts the State machine with the given login configuration.
     *
     * @param loginConfiguration the login configuration
     */
    private static void createEndpointAndRun(LoginConfiguration loginConfiguration) {
        final ToolStateMachine stateMachine = new ToolStateMachine(loginConfiguration);
        addShutdownHook(stateMachine);
        stateMachine.start();
    }

    /**
     * Adding a shutdown hook to perform a logout if the user quits the program incorrectly or
     * it is shut down by another process of the system.
     *
     * @param stateMachine the statemachine
     */
    private static void addShutdownHook(final ToolStateMachine stateMachine) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (stateMachine != null) {
                    UserDataIds userDataIds = stateMachine.getUserDataIds();
                    ILIASSoapWebservicePortType endPoint = stateMachine.getEndPoint();
                    if (userDataIds != null && endPoint != null) {
                        try {
                            boolean isLoggedOut = endPoint.logout(userDataIds.getSid());
                            if (isLoggedOut) {
                                LOG.info("Successfully logged out for sid: '" + userDataIds.getSid() + "' before shutting down!");
                            } else {
                                LOG.error("Could not log out on shutdown!");
                            }
                        } catch (RemoteException e) {
                            LOG.error("Could not log out on shutdown: " + e.getMessage());
                        }
                    }
                } else {
                    LOG.info("No connection had to be closed on shutdown hook!");
                }
            }
        });
    }
}
