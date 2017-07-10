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

import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.wrapper.IliasEndpoint;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * The state performing the quit of the application and the logout at the endpoint.
 * <p>
 * Created by mavogel on 9/7/16.
 */
public class QuitState extends ToolState {

    private static Logger LOG = Logger.getLogger(QuitState.class);

    /**
     * Creates the state for quitting.
     *
     * @param stateMachine the state machine
     */
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
    protected void doExecute(final IliasAction nodesAndActions) {
        IliasEndpoint endpoint = this.stateMachine.getEndpoint();
        try {
            endpoint.logout("");
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                LOG.error("No log out needed");
            } else {
                LOG.error("Could not log out: " + e.getMessage());
            }
        }
        this.stateMachine.stop();
    }
}
