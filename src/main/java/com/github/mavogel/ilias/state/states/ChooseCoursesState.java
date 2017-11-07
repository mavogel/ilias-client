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
import com.github.mavogel.ilias.lib.model.IliasNode;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.Defaults;
import com.github.mavogel.ilias.utils.IOUtils;
import com.github.mavogel.ilias.lib.wrapper.DisplayStatus;
import com.github.mavogel.ilias.lib.wrapper.IliasEndpoint;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The state for choosing courses.
 * <p>
 * Created by mavogel on 9/7/16.
 */
public class ChooseCoursesState extends ToolState {

    private static Logger LOG = Logger.getLogger(ChooseCoursesState.class);

    /**
     * Creates the choose courses state.
     *
     * @param stateMachine the state machine
     * @param successors   its successors
     */
    public ChooseCoursesState(final ToolStateMachine stateMachine, final ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    public void printInformation() {
        LOG.info("Choose a Course:");
        this.stateMachine.getContext().remove(ToolStateMachine.ContextKey.GROUPS);
    }

    @Override
    protected List<IliasNode> collectDataForExecution() {
        IliasEndpoint iliasEndpoint = stateMachine.getEndpoint();
        try {
            return iliasEndpoint.getCoursesForUser(DisplayStatus.ADMIN);
        } catch (Exception e) {
            this.stateMachine.setState(stateMachine.getQuitState());
        }

        return Collections.emptyList();
    }

    @Override
    protected IliasAction printAndParseExecutionChoices(final List<IliasNode> nodeChoices) {
        if(nodeChoices.isEmpty()) {
            LOG.info("======================== HINT =========================");
//            LOG.info("Found no courses you're admin of with userId '" + stateMachine.getUserDataIds().getUsername() + "'");
            LOG.info("Found no courses you're admin of ");
            LOG.info("Please add your user in the Ilias GUI as admin");
            LOG.info("to at least one course and enter 'ChooseCourses' again");
            LOG.info("=======================================================");
            return new IliasAction();
        }

        LOG.info(Defaults.GET_CHOICE_AROUND());
        IntStream.range(0, nodeChoices.size())
                .mapToObj(i -> nodeChoices.get(i).asDisplayString(Defaults.GET_CHOICE_PREFIX(i)))
                .forEach(LOG::info);
        LOG.info(Defaults.GET_CHOICE_AROUND());

        // TODO only single nodes atm due to printing or change context keys
        // IOUtils.readAndParseChoicesFromUser(nodeChoices);
        int singleChoice = IOUtils.readAndParseSingleChoiceFromUser(nodeChoices);
        List<Integer> indexesOfChosenNodes = new ArrayList<>();
        indexesOfChosenNodes.add(singleChoice);

        List<IliasNode> chosenIliasNodes = indexesOfChosenNodes.stream()
                .map(idx -> nodeChoices.get(idx))
                .collect(Collectors.toList());

        return new IliasAction(chosenIliasNodes, null);
    }

    @Override
    protected void doExecute(final IliasAction nodesAndActions) {
        if(!nodesAndActions.isCompletelyEmpty()) {
            stateMachine.getContext().put(ToolStateMachine.ContextKey.COURSES, nodesAndActions.getNodes());
        }
    }
}
