package com.github.mavogel.ilias.state;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.state.states.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mavogel on 9/6/16.
 */
public class ToolStateMachine {

    private boolean isInEndState;

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
        GROUPS
    }

    private UserDataIds userDataId;
    private ILIASSoapWebservicePortType endPoint;
    private int maxFolderDepth;

    private Map<ContextKey, List<IliasNode>> context;

    private ToolState startState;
    private ToolState loginState;
    private ToolState chooseCoursesState;
    private ToolState actionsOnGroupsState;
    private ToolState quitState;

    private ToolState currentState;

    public ToolStateMachine(final LoginConfiguration loginConfiguration) {
        this.maxFolderDepth = loginConfiguration.getMaxFolderDepth();
        this.isInEndState = false;
        this.context = new HashMap<>();

        // encoding of the state machine
        this.quitState = new QuitState(this);

        this.actionsOnGroupsState = new ActionsOnGroupsState(this);
        this.chooseCoursesState = new ChooseCoursesState(this, this.actionsOnGroupsState, this.quitState);
        this.actionsOnGroupsState.setSuccessors(this.chooseCoursesState, this.quitState); // because of null ref

        this.loginState = new LoginState(this, loginConfiguration, this.chooseCoursesState);
        this.startState = new StartState(this, this.loginState);

        setState(this.startState);
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
     * @return the maximum folder depth
     */
    public int getMaxFolderDepth() {
        return maxFolderDepth;
    }

    /**
     * @return the current context
     */
    public Map<ContextKey, List<IliasNode>> getContext() {
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

    /////////////////////////////////////
    // Starter and stopper
    /////////////////////////////////////
    /**
     * Starts the state machine until the {@link QuitState} is left.
     */
    public void start() {
        while (!isInEndState) {
            this.currentState.printInformation();
            this.currentState.execute();
            this.currentState.transition();
        }
    }

    /**
     * Stops the state machine
     */
    public void stop() {
        isInEndState = true;
    }

    ///////////////////////////////
    /////////// States ////////////
    ///////////////////////////////

    public ToolState getStartState() {
        return startState;
    }

    public ToolState getLoginState() {
        return loginState;
    }

    public ToolState getChooseCoursesState() {
        return chooseCoursesState;
    }

    public ToolState getActionsOnGroupsState() {
        return actionsOnGroupsState;
    }

    public ToolState getQuitState() {
        return quitState;
    }
}
