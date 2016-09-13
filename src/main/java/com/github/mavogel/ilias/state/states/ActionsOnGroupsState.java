package com.github.mavogel.ilias.state.states;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.state.ChangeAction;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.state.states.action.RemoveUploadedMaterialsChange;
import com.github.mavogel.ilias.state.states.action.RemoveUsersChange;
import com.github.mavogel.ilias.state.states.action.SetRegistrationPeriodChange;
import com.github.mavogel.ilias.utils.IOUtils;
import com.github.mavogel.ilias.utils.IliasUtils;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mavogel on 9/7/16.
 */
public class ActionsOnGroupsState extends ToolState {

    private static Logger LOG = Logger.getLogger(ActionsOnGroupsState.class);

    public ActionsOnGroupsState(final ToolStateMachine stateMachine, final ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    public void printInformation() {
        LOG.info("Choose one or more actions on groups:");
    }

    @Override
    protected List<IliasNode> collectDataForExecution() {
        ILIASSoapWebservicePortType endpoint = stateMachine.getEndPoint();
        final String sid = stateMachine.getUserDataIds().getSid();
        final int userId = stateMachine.getUserDataIds().getUserId();
        final int maxFolderDepth = stateMachine.getMaxFolderDepth();

        final List<IliasNode> courseRefIds = stateMachine.getContext().get(ToolStateMachine.ContextKey.COURSES);
        try {
            return IliasUtils.retrieveGroupRefIdsFromCourses(endpoint, sid, userId,
                    courseRefIds.stream().map(c -> c.getRefId()).collect(Collectors.toList()),
                    maxFolderDepth);
        } catch (JDOMException | IOException e) {
            LOG.error("Error creating xml parser: " + e.getMessage());
            this.stateMachine.setState(stateMachine.getChooseCoursesState());
        }
        return Collections.emptyList();
    }

    @Override
    protected IliasAction printAndParseExecutionChoices(final List<IliasNode> nodeChoices) {
        IntStream.range(0, nodeChoices.size())
                .mapToObj(i -> nodeChoices.get(i).asDisplayString(i + ") "))
                .forEach(LOG::info);

        List<Integer> indexesOfChosenNodes = IOUtils.readAndParseChoicesFromUser(nodeChoices);
        List<IliasNode> choseIliasNodes = indexesOfChosenNodes.stream()
                .map(idx -> nodeChoices.get(idx))
                .collect(Collectors.toList());

        // Set actions
        List<ChangeAction> actionChoices = Arrays.asList(new RemoveUploadedMaterialsChange(),
                new RemoveUsersChange(), new SetRegistrationPeriodChange());

        IntStream.range(0, actionChoices.size())
                .mapToObj(i -> actionChoices.get(i).actionName(i + ") "))
                .forEach(LOG::info);
        List<Integer> indexesOfChosenActions = IOUtils.readAndParseChoicesFromUser(actionChoices);
        List<ChangeAction> choseActions = indexesOfChosenActions.stream()
                .map(idx -> actionChoices.get(idx))
                .collect(Collectors.toList());
        return new IliasAction(choseIliasNodes, choseActions);
    }

    @Override
    protected String doExecute(final IliasAction nodesAndActions) {
        final ILIASSoapWebservicePortType endpoint = stateMachine.getEndPoint();
        UserDataIds userDataIds = stateMachine.getUserDataIds();
        List<IliasNode> nodes = nodesAndActions.getNodes();

        nodesAndActions.getActions().stream().forEach(action -> action.performAction(endpoint, userDataIds, nodes));
        return "";
    }
}
