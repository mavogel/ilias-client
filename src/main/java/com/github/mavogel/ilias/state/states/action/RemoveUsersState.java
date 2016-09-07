package com.github.mavogel.ilias.state.states.action;

import com.github.mavogel.ilias.state.ExecutionState;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;

/**
 * Created by mavogel on 9/7/16.
 */
public class RemoveUsersState extends ToolState implements ExecutionState {

    public RemoveUsersState(final ToolStateMachine stateMachine, final ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    protected void printInformation() {
        System.out.println("Removing users");
    }

    @Override
    protected void printExecutionChoices() {

    }

    @Override
    protected void printExecutionPreview() {

    }

    @Override
    protected boolean confirm() {
        return false;
    }

    @Override
    protected void execute() {

    }

    @Override
    protected void printExecutionSummary() {

    }

    @Override
    protected void parseExecutionChoices() {

    }
}
