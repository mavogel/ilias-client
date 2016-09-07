package com.github.mavogel.ilias.state.states;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.IliasUtils;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mavogel on 9/7/16.
 */
public class ActionsOnGroupsState extends ToolState {

    public ActionsOnGroupsState(final ToolStateMachine stateMachine, final ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    public void printInformation() {
        System.out.println("Actions on Groups:");
    }

    @Override
    protected void collectDataForExecution() {
        ILIASSoapWebservicePortType endpoint = stateMachine.getEndPoint();
        final String sid = stateMachine.getUserDataIds().getSid();
        final int userId = stateMachine.getUserDataIds().getUserId();
        final int maxFolderDepth = 3;

        final List<IliasNode> courseRefIds = stateMachine.getContext().get(ToolStateMachine.ContextKey.COURSES);
        try {
            // TODO get ilias nodes as well here
            List<Integer> groupRefIdsFromCourses = IliasUtils.retrieveGroupRefIdsFromCourses(endpoint, sid, userId,
                    courseRefIds.stream().map(c -> c.getRefId()).collect(Collectors.toList()),
                    maxFolderDepth);// TODO get from logconf
        } catch (JDOMException | IOException e) {
            System.err.println("Error creating xml parser: " + e.getMessage());
            this.stateMachine.setState(stateMachine.getChooseCoursesState());
        }
    }
}
