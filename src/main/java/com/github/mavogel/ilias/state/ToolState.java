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
package com.github.mavogel.ilias.state;

import com.github.mavogel.ilias.lib.model.IliasNode;
import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.utils.IOUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Base class for all state of the tool state machine. Use by it as well.
 *
 * Created by mavogel on 9/6/16.
 */
public abstract class ToolState {

    private static Logger LOG = Logger.getLogger(ToolState.class);

    protected ToolStateMachine stateMachine;
    protected List<ToolState> successors;

    /**
     * C'tor for a {@link ToolState}
     *
     * @param stateMachine the {@link ToolStateMachine}
     */
    protected ToolState(final ToolStateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    /**
     * Sets the successors from a var args. Ensures correct initialization of the
     * member variable.
     *
     * @param successors the successors
     */
    protected void setSuccessors(ToolState... successors) {
        if (successors == null || successors.length == 0) {
            this.successors = new ArrayList<>();
        } else {
            this.successors = Arrays.stream(successors)
                    .filter(s -> s != null)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Prints the information of the current state.
     */
    protected abstract void printInformation();

    /**
     * Prints and parses the possible choices for transitions.
     *
     * @return the index of the successors which was chosen, or <b>-1</b> for a state which has no successors.
     */
    protected int printAndParseTransitionChoices() {
        if (!successors.isEmpty()) {
            LOG.info("Next step:");
            IntStream.range(0, successors.size())
                    .mapToObj(i -> successors.get(i).asDisplayString(i + ") "))
                    .forEach(LOG::info);
            return IOUtils.readAndParseSingleChoiceFromUser(successors);
        } else {
            return -1;
        }
    }

    /**
     * Performs the transition to the next state.
     */
    protected void transition() {
        if(!successors.isEmpty()) {
            final int transitionChoice = printAndParseTransitionChoices();
            stateMachine.setState(successors.get(transitionChoice));
        }
    }

    /**
     * Returns a string for displaying the information of the state
     * on the command line.
     *
     * @param prefix an optional header
     * @return the string
     */
    public String asDisplayString(final String prefix) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null && !prefix.isEmpty()) sb.append(prefix);
        sb.append(this.getClass().getSimpleName().replace("State", ""));
        return sb.toString();
    }

    //////////////////////////////////
    /////////// EXECUTIONS ///////////
    //////////////////////////////////

    /**
     * Collects data for the execution. Usually these are refId of object to perform actions on later.
     *
     * @return the nodes for the execution
     */
    protected abstract List<IliasNode> collectDataForExecution();

    /**
     * Parses the given choices for execution.
     *
     * @param nodeChoices the node the user can choose from
     * @return the chosen nodes with the desired actions to perform
     */
    protected abstract IliasAction printAndParseExecutionChoices(List<IliasNode> nodeChoices);

    /**
     * Executes the command of the state.
     */
    public void execute() {
        List<IliasNode> nodeChoices = this.collectDataForExecution();
        IliasAction chosenNodesAndActions = this.printAndParseExecutionChoices(nodeChoices);
        this.doExecute(chosenNodesAndActions);
        LOG.info("----------------------------------------");
    }

    /**
     * Internal execute
     *
     * @param nodesAndActions the nodes and actions
     */
    protected abstract void doExecute(final IliasAction nodesAndActions);
}
