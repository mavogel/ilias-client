package com.github.mavogel.ilias.state.states.action;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.state.ExecutionState;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.IliasUtils;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mavogel on 9/7/16.
 */
public class RemoveUsersState extends ToolState implements ExecutionState {

    private List<IliasNode> selectedGroups;
    private List<Integer> indexesOfChosenGroups;

    public RemoveUsersState(final ToolStateMachine stateMachine, final ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    protected void printInformation() {
        System.out.println("Removing users from Groups");
    }

    @Override
    protected void collectDataForExecution() {
        this.selectedGroups = stateMachine.getContext().get(ToolStateMachine.ContextKey.GROUPS);
    }

    @Override
    protected void printExecutionChoices() {
        String executionChoices = IntStream.range(0, this.selectedGroups.size())
                .mapToObj(i -> this.selectedGroups.get(i).asDisplayString(i + ") "))
                .collect(Collectors.joining("\n"));
        System.out.println(executionChoices);
    }

    @Override
    protected void parseExecutionChoices() {
        this.indexesOfChosenGroups = super.parseUserChoices(this.selectedGroups);
    }

    @Override
    protected void printExecutionPreview() {
        System.out.println("Will remove users of " + indexesOfChosenGroups.size() + " groups!");
    }

    @Override
    protected boolean confirm() {
        // TODO user choices
        return false;
    }

    @Override
    protected void execute() {
        this.selectedGroups = this.indexesOfChosenGroups.stream()
                .map(idx -> this.selectedGroups.get(idx))
                .collect(Collectors.toList());

        ILIASSoapWebservicePortType endpoint = stateMachine.getEndPoint();
        final String sid = stateMachine.getUserDataIds().getSid();
        try {
            IliasUtils.removeAllMembersFromGroups(endpoint, sid, selectedGroups);
        } catch (IOException | JDOMException e) {
            System.err.println("Error creating xml parser: " + e.getMessage());
            this.stateMachine.setState(stateMachine.getQuitState());
        }
    }

    @Override
    protected void printExecutionSummary() {
        // done internally in IliasUtils.removeAllMembersFromGroups
    }
}
