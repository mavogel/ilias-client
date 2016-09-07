package com.github.mavogel.ilias.state.states;

import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;

import java.util.Arrays;
import java.util.List;

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

    }
}
