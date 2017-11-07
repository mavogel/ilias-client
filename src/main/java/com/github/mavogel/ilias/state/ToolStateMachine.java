/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016 Manuel Vogel
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 *  https://opensource.org/licenses/MIT
 */
package com.github.mavogel.ilias.state;

import com.github.mavogel.ilias.lib.model.IliasNode;
import com.github.mavogel.ilias.lib.model.LoginConfiguration;
import com.github.mavogel.ilias.lib.model.UserDataIds;
import com.github.mavogel.ilias.state.states.*;
import com.github.mavogel.ilias.lib.wrapper.AbstractIliasEndpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by mavogel on 9/6/16.
 */
public class ToolStateMachine {

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

    private AbstractIliasEndpoint endpoint;
    private Map<ContextKey, List<IliasNode>> context;

    private boolean isInEndState;
    private ToolState startState;
    private ToolState loginState;
    private ToolState chooseCoursesState;
    private ToolState actionsOnGroupsState;
    private ToolState quitState;

    private ToolState currentState;

    /**
     * Creates the state machine of the tool.
     *
     * @param loginConfiguration the configuration of the login
     */
    public ToolStateMachine(final LoginConfiguration loginConfiguration) {
        this.isInEndState = false;
        this.context = new HashMap<>();

        // encoding of the state machine
        this.quitState = new QuitState(this);
        this.actionsOnGroupsState = new ActionsOnGroupsState(this);
        this.chooseCoursesState = new ChooseCoursesState(this);

        this.chooseCoursesState.setSuccessors(this.chooseCoursesState, this.actionsOnGroupsState, this.quitState);
        this.actionsOnGroupsState.setSuccessors(this.chooseCoursesState, this.quitState);

        this.loginState = new LoginState(this, loginConfiguration, this.chooseCoursesState);
        this.startState = new StartState(this, this.loginState);

        setState(this.startState);
    }

    /**
     * @return the user data ids
     */
    public Optional<UserDataIds> getUserDataIds() {
        if (this.endpoint != null) {
            return Optional.of(this.endpoint.getUserDataIds());
        } else {
            return Optional.empty();
        }
    }

    /**
     * @return the endpoint
     */
    public AbstractIliasEndpoint getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint
     */
    public void setEndpoint(final AbstractIliasEndpoint endpoint) {
        this.endpoint = endpoint;
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

    public ToolState getChooseCoursesState() {
        return chooseCoursesState;
    }

    public ToolState getQuitState() {
        return quitState;
    }

    public boolean isInEndState() {
        return isInEndState;
    }
}
