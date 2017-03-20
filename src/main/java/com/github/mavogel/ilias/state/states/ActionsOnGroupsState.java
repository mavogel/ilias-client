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
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.state.ChangeAction;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.state.states.action.*;
import com.github.mavogel.ilias.utils.Defaults;
import com.github.mavogel.ilias.utils.IOUtils;
import com.github.mavogel.ilias.utils.IliasUtils;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The state for actions on groups.
 * <p>
 * Created by mavogel on 9/7/16.
 */
public class ActionsOnGroupsState extends ToolState {

    private static Logger LOG = Logger.getLogger(ActionsOnGroupsState.class);

    /**
     * The choices on groups.
     */
    private static final List<ChangeAction> ACTION_CHOICES = Arrays.asList(
            new RemoveUploadedMaterialsAction(),
            new RemoveUsersAction(),
            new SetRegistrationPeriodAction(),
            new SetMaxMembersAction(),
            new GrantFileUploadToGroupMembersAction(),
            new PrintGroupMembersAction()
    );

    /**
     * Creates the action on groups state.
     *
     * @param stateMachine the state machine
     * @param successors   its successors
     */
    public ActionsOnGroupsState(final ToolStateMachine stateMachine, final ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    public void printInformation() {
        LOG.info("Choose one or more actions on groups when collection is finished!");
    }

    @Override
    protected List<IliasNode> collectDataForExecution() {
        ILIASSoapWebservicePortType endpoint = stateMachine.getEndPoint();
        final String sid = stateMachine.getUserDataIds().getSid();
        final int userId = stateMachine.getUserDataIds().getUserId();
        final int maxFolderDepth = stateMachine.getMaxFolderDepth();

        final List<IliasNode> courses = stateMachine.getContext().get(ToolStateMachine.ContextKey.COURSES);
        try {
            return IliasUtils.retrieveGroupRefIdsFromCourses(endpoint, sid, userId, courses, maxFolderDepth);
        } catch (JDOMException | IOException e) {
            LOG.error("Error creating xml parser: " + e.getMessage());
            this.stateMachine.setState(stateMachine.getChooseCoursesState());
        }
        return Collections.emptyList();
    }

    @Override
    protected IliasAction printAndParseExecutionChoices(final List<IliasNode> nodeChoices) {
        LOG.info(Defaults.GET_CHOICE_AROUND());
        IntStream.range(0, nodeChoices.size())
                .mapToObj(i -> nodeChoices.get(i).asDisplayString(Defaults.GET_CHOICE_PREFIX(i)))
                .forEach(LOG::info);
        LOG.info(Defaults.GET_CHOICE_AROUND());
        List<Integer> indexesOfChosenNodes = IOUtils.readAndParseChoicesFromUser(nodeChoices);
        List<IliasNode> choseIliasNodes = indexesOfChosenNodes.stream()
                .map(idx -> nodeChoices.get(idx))
                .collect(Collectors.toList());

        LOG.info(Defaults.GET_CHOICE_AROUND());
        IntStream.range(0, ACTION_CHOICES.size())
                .mapToObj(i -> ACTION_CHOICES.get(i).actionName(Defaults.GET_CHOICE_PREFIX(i)))
                .forEach(LOG::info);
        LOG.info(Defaults.GET_CHOICE_AROUND());
        List<Integer> indexesOfChosenActions = IOUtils.readAndParseChoicesFromUser(ACTION_CHOICES);
        List<ChangeAction> choseActions = indexesOfChosenActions.stream()
                .map(idx -> ACTION_CHOICES.get(idx))
                .collect(Collectors.toList());
        return new IliasAction(choseIliasNodes, choseActions);
    }

    @Override
    protected void doExecute(final IliasAction nodesAndActions) {
        final ILIASSoapWebservicePortType endpoint = stateMachine.getEndPoint();
        Map<ToolStateMachine.ContextKey, List<IliasNode>> context = stateMachine.getContext();
        UserDataIds userDataIds = stateMachine.getUserDataIds();
        List<IliasNode> nodes = nodesAndActions.getNodes();

        nodesAndActions.getActions().stream().forEach(action -> action.performAction(endpoint, context, userDataIds, nodes));
    }
}
