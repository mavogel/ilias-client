package com.github.mavogel.ilias.state;

import com.github.mavogel.ilias.state.states.StartState;

/**
 * Created by mavogel on 9/6/16.
 */
public class ToolStateMachine {

    private ToolState startState;
    private ToolState chooseCoursesState;
    private ToolState chooseTasksState;
    private ToolState removeUsersState;
    private ToolState setRegistrationState;
    private ToolState readRegistrationsState;
    private ToolState removeMaterialsState;
    private ToolState quitState;

    private ToolState currentState;

    public ToolStateMachine() {
        this.startState = new StartState(this);

        setState(startState);
    }

    /**
     * Sets the current state.
     *
     * @param state the current state to set
     */
    public void setState(ToolState state) {
        this.currentState = state;
    }

    /**
     * Performs the actions on entering the state.
     */
    public void enterState() {
        this.currentState.printInformation();
        this.currentState.printExecutionChoices();
        this.currentState.printExectionSummary();
        this.currentState.printTransitionChoices();
    }

    /**
     * Performs the actions on state execution.
     */
    public void executeState() {
        this.currentState.parseExecutionChoices();
        this.currentState.printExecutionPreview();
        boolean isConfirmed = this.currentState.confirm();
        if (isConfirmed) {
            this.currentState.execute();
        }
    }

    /**
     * Perform actions before leaving the state and the does the transition.
     */
    public void leaveState() {
        this.currentState.printTransitionChoices();
        this.currentState.parseTransitionChoice();
        this.currentState.transition();
    }

    public ToolState getStartState() {
        return startState;
    }

    public ToolState getChooseCoursesState() {
        return chooseCoursesState;
    }

    public ToolState getChooseTasksState() {
        return chooseTasksState;
    }

    public ToolState getRemoveUsersState() {
        return removeUsersState;
    }

    public ToolState getSetRegistrationState() {
        return setRegistrationState;
    }

    public ToolState getReadRegistrationsState() {
        return readRegistrationsState;
    }

    public ToolState getRemoveMaterialsState() {
        return removeMaterialsState;
    }

    public ToolState getQuitState() {
        return quitState;
    }
}
