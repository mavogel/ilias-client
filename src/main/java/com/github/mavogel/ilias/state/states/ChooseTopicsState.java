package com.github.mavogel.ilias.state.states;

import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.IliasUtils;

/**
 * Created by mavogel on 9/6/16.
 */
public class ChooseTopicsState extends ToolState {

    public ChooseTopicsState(final ToolStateMachine stateMachine, final ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    public void printInformation() {
        System.out.println("Choose a topic");
    }
}
