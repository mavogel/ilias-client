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
import java.util.stream.IntStream;

/**
 * Created by mavogel on 9/7/16.
 */
public class ActionsOnGroupsState extends ToolState {

    private List<IliasNode> groupNodesFromCourses;
    private List<Integer> indexesOfChosenCourses;

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
        if (stateMachine.getContext().containsKey(ToolStateMachine.ContextKey.GROUPS)) {
            this.groupNodesFromCourses = stateMachine.getContext().get(ToolStateMachine.ContextKey.GROUPS);
        } else {
            ILIASSoapWebservicePortType endpoint = stateMachine.getEndPoint();
            final String sid = stateMachine.getUserDataIds().getSid();
            final int userId = stateMachine.getUserDataIds().getUserId();
            final int maxFolderDepth = 3;

            final List<IliasNode> courseRefIds = stateMachine.getContext().get(ToolStateMachine.ContextKey.COURSES);
            try {
                this.groupNodesFromCourses = IliasUtils.retrieveGroupRefIdsFromCourses(endpoint, sid, userId,
                        courseRefIds.stream().map(c -> c.getRefId()).collect(Collectors.toList()),
                        maxFolderDepth);// TODO get from logconf
            } catch (JDOMException | IOException e) {
                System.err.println("Error creating xml parser: " + e.getMessage());
                this.stateMachine.setState(stateMachine.getChooseCoursesState());
            }
        }
    }

    @Override
    protected void printExecutionChoices() {
        String executionChoices = IntStream.range(0, this.groupNodesFromCourses.size())
                .mapToObj(i -> this.groupNodesFromCourses.get(i).asDisplayString(i + ") "))
                .collect(Collectors.joining("\n"));
        System.out.println(executionChoices);
    }

    @Override
    protected void parseExecutionChoices() {
        this.indexesOfChosenCourses = super.parseUserChoices(this.groupNodesFromCourses);
    }

    @Override
    protected void execute() {
        stateMachine.getContext().put(ToolStateMachine.ContextKey.GROUPS,
                this.indexesOfChosenCourses.stream()
                        .map((idx -> this.groupNodesFromCourses.get(idx)))
                        .collect(Collectors.toList()));
    }
}
