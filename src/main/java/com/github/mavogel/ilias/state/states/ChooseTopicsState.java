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

    @Override
    protected void collectDataForExecution() {
//        IliasUtils.
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