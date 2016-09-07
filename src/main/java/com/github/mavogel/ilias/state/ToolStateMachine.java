package com.github.mavogel.ilias.state;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.state.states.*;
import com.github.mavogel.ilias.state.states.action.RemoveUsersState;

import java.util.*;

/**
 * Created by mavogel on 9/6/16.
 */
public class ToolStateMachine {

    private boolean isInEndState;

    /**
     * Starts the statemachin until the {@link QuitState} is left.
     */
    public void start() {
        while (!isInEndState) {
            this.enterState();
            this.executeState();
            this.leaveState();
        }
    }

    /**
     * Stops the state machine
     */
    public void stop() {
        isInEndState = true;
    }

    /**
     * The keys for the context shared between the states.
     */
    public enum ContextKey {
        /**
         * The refIds of the chosen courses
         */
        COURSES,

        /**
         * The refIds of the chosen groups
         */
        GROUPS;
    }

    private UserDataIds userDataId;
    private ILIASSoapWebservicePortType endPoint;

    private Map<ContextKey, List<Integer>> context;

    private ToolState startState;
    private ToolState loginState;
    private ToolState chooseTopicsState;
    private ToolState chooseCoursesState;
    private ToolState chooseTasksState;
    private ToolState removeUsersState;
    private ToolState setRegistrationState;
    private ToolState removeMaterialsState;
    private ToolState quitState;

    private ToolState currentState;

    public ToolStateMachine(final LoginConfiguration loginConfiguration) {
        this.isInEndState = false;
        this.context = new HashMap<>();

        this.quitState = new QuitState(this);
        this.removeUsersState = new RemoveUsersState(this);
        this.chooseTasksState = new ActionsOnGroupsState(this, removeUsersState);
        this.chooseTopicsState = new ChooseTopicsState(this, chooseTasksState, quitState);
        this.chooseCoursesState = new ChooseCoursesState(this, chooseTopicsState, quitState);
        this.loginState = new LoginState(this, loginConfiguration, chooseCoursesState, quitState);
        this.startState = new StartState(this, loginState, quitState);

        setState(startState);
    }

    /**
     * @return the user data ids
     */
    public UserDataIds getUserDataIds() {
        return userDataId;
    }

    /**
     * @param userDataId the user data ids
     */
    public void setUserDataId(final UserDataIds userDataId) {
        this.userDataId = userDataId;
    }

    /**
     * @return the ws endpoint
     */
    public ILIASSoapWebservicePortType getEndPoint() {
        return endPoint;
    }

    /**
     * @param endPoint the ws endpoint
     */
    public void setEndPoint(final ILIASSoapWebservicePortType endPoint) {
        this.endPoint = endPoint;
    }

    /**
     * @return the current context
     */
    public Map<ContextKey, List<Integer>> getContext() {
        return context;
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
    }

    /**
     * Performs the actions on state execution.
     */
    public void executeState() {
        this.currentState.collectDataForExecution();
        this.currentState.parseExecutionChoices();
        this.currentState.printExecutionPreview();
        boolean isConfirmed = this.currentState.confirm();
        if (isConfirmed) {
            this.currentState.execute();
        }
        this.currentState.printExecutionSummary();
    }

    /**
     * Perform actions before leaving the state and the does the transition.
     */
    public void leaveState() {
        this.currentState.printTransitionChoices();
        this.currentState.parseTransitionChoice();
        this.currentState.transition();
    }


    /////////// States ////////////

    public ToolState getStartState() {
        return startState;
    }

    public ToolState getChooseTopicsState() {
        return chooseTopicsState;
    }

    public ToolState getLoginState() {
        return loginState;
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

    public ToolState getRemoveMaterialsState() {
        return removeMaterialsState;
    }

    public ToolState getQuitState() {
        return quitState;
    }
}