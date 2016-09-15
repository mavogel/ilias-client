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
package com.github.mavogel.ilias.state.states;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.IliasUtils;
import org.apache.log4j.Logger;

import javax.xml.rpc.ServiceException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

/**
 * The state performing the login.
 *
 * Created by mavogel on 9/7/16.
 */
public class LoginState extends ToolState {

    private static Logger LOG = Logger.getLogger(LoginState.class);

    private LoginConfiguration loginConfiguration;

    /**
     * /**
     * Creates the action on groups state.
     *
     * @param stateMachine the state machine
     * @param loginConfiguration the configuration of the login
     * @param successors its successors
     */
    public LoginState(final ToolStateMachine stateMachine,
                      final LoginConfiguration loginConfiguration,
                      final ToolState... successors) {
        super(stateMachine);
        this.loginConfiguration = loginConfiguration;
        setSuccessors(successors);
    }

    @Override
    protected void printInformation() {
        LOG.info("Logging in and getting user data...");
    }

    @Override
    protected int printAndParseTransitionChoices() {
        return 0;
    }

    @Override
    protected List<IliasNode> collectDataForExecution() {
        return Collections.emptyList();
    }

    @Override
    protected IliasAction printAndParseExecutionChoices(final List<IliasNode> nodeChoices) {
        return new IliasAction();
    }

    @Override
    protected String doExecute(final IliasAction nodesAndActions) {
        try {
            ILIASSoapWebservicePortType endpoint = IliasUtils.createWsEndpoint(loginConfiguration);
            stateMachine.setEndPoint(endpoint);
            stateMachine.setUserDataId(IliasUtils.getUserData(loginConfiguration, endpoint));
            return String.format("Logged in successfully as '%s'", loginConfiguration.getUsername());
        } catch (ServiceException e) {
            LOG.error("Could not establish webservice at '" + loginConfiguration.getEndpoint() + "'");
            stateMachine.setState(stateMachine.getQuitState());
            return "";
        } catch (RemoteException e) {
            Throwable cause = e.getCause();
            if (cause instanceof UnknownHostException) {
                LOG.error("Could not establish connection to endpoint '" + loginConfiguration.getEndpoint()+ "'");
            } else {
                LOG.error("Error retrieving the user data: " + e.getMessage());
            }
            stateMachine.setState(stateMachine.getQuitState());
            return "";
        }
    }
}
