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
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * The state for the start of the application.
 *
 * Created by mavogel on 9/6/16.
 */
public class StartState extends ToolState {

    private static Logger LOG = Logger.getLogger(StartState.class);

    /**
     * Creates the start state.
     *
     * @param stateMachine the state machine
     * @param successors its successors
     */
    public StartState(final ToolStateMachine stateMachine, ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    public void printInformation() {
          LOG.info(" _______ __                       ");
          LOG.info("|       |  |--.-----.             ");
          LOG.info("|.|   | |     |  -__|             ");
          LOG.info("`-|.  |-|__|__|_____|             ");
          LOG.info("  |:  |                           ");
          LOG.info("  |::.|                           ");
          LOG.info("  `---'                           ");
          LOG.info(" ___ ___     ___ _______ _______  ");
          LOG.info("|   |   |   |   |   _   |   _   | ");
          LOG.info("|.  |.  |   |.  |.  1   |   1___| ");
          LOG.info("|.  |.  |___|.  |.  _   |____   | ");
          LOG.info("|:  |:  1   |:  |:  |   |:  1   | ");
          LOG.info("|::.|::.. . |::.|::.|:. |::.. . | ");
          LOG.info("`---`-------`---`--- ---`-------' ");
          LOG.info(" _______ __ __             __     ");
          LOG.info("|   _   |  |__.-----.-----|  |_   ");
          LOG.info("|.  1___|  |  |  -__|     |   _|  ");
          LOG.info("|.  |___|__|__|_____|__|__|____|  ");
          LOG.info("|:  1   |                         ");
          LOG.info("|::.. . |                         ");
          LOG.info("`-------'                         ");
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
    protected void doExecute(final IliasAction nodesAndActions) { }
}
