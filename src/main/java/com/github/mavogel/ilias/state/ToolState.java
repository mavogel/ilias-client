package com.github.mavogel.ilias.state;

import java.util.List;

/**
 * Created by mavogel on 9/6/16.
 */
public interface ToolState {

    /**
     * Prints the information of the current state.
     */
    void printInformation();

    /**
     * Prints the possible execution choices for the state.
     */
    void printExecutionChoices();

    /**
     * Prints a small summary of the amount of affected nodes/objects of the execution
     */
    void printExecutionPreview();

    /**
     * Prints and requests a confirmation from the user for the upcoming action.
     */
    boolean confirm();

    /**
     * Executes the command of the state if there is one
     */
    void execute();

    /**
     * Prints the summary of the preceding execution.
     */
    void printExectionSummary();

    /**
     * Parses the given choices for execution
     */
    void parseExecutionChoices();

    /**
     * Prints the possible choices for transitions
     */
    void printTransitionChoices();


    /**
     *  Parses the given choices for the transition
     */
    void parseTransitionChoice();

    /**
     * Performs the transition to the next state
     */
    void transition();

    /**
     * Returns a string for displaying the information of the state
     * on the command line
     *
     * @param prefix an optional prefix
     * @return the string
     */
    String asDisplayString(final String prefix);
}
