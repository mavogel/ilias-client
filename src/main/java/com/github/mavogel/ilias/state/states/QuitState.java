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
package com.github.mavogel.ilias.state.states;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

/**
 * Created by mavogel on 9/7/16.
 */
public class QuitState extends ToolState {

    private static Logger LOG = Logger.getLogger(QuitState.class);

    public QuitState(final ToolStateMachine stateMachine) {
        super(stateMachine);
        this.setSuccessors();
    }

    @Override
    public void printInformation() {
        LOG.info("Quitting... bye bye!");
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
        ILIASSoapWebservicePortType endPoint = this.stateMachine.getEndPoint();
        UserDataIds userDataIds = this.stateMachine.getUserDataIds();
        if (userDataIds != null && endPoint != null) {
            try {
                boolean isLoggedOut = endPoint.logout(userDataIds.getSid());
                if (isLoggedOut) {
                    LOG.info("Successfully logged out for sid: '" + userDataIds.getSid() + "'");
                } else {
                    LOG.error("Could not log out!");
                }
            } catch (RemoteException e) {
                LOG.error("Could not log out: " + e.getMessage());
            }
        }
        this.stateMachine.stop();
        return "";
    }
}
