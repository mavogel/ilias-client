package com.github.mavogel.ilias.state;

import com.github.mavogel.ilias.utils.IOUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mavogel on 9/6/16.
 */
public abstract class ToolState {

    protected ToolStateMachine stateMachine;

    protected List<ToolState> successors;
    protected int transitionChoice;

    protected List<ExecutionState> executionStates;
    private List<Integer> executionChoices;

    /**
     * C'tor for a {@link ToolState}
     *
     * @param stateMachine the {@link ToolStateMachine}
     */
    protected ToolState(final ToolStateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    /**
     * Sets the successors from a var args. Ensures correct initialization of the
     * memeber variable.
     *
     * @param successors the successors
     */
    protected void setSuccessors(ToolState... successors) {
        if (successors == null || successors.length == 0) {
            this.successors = new ArrayList<>();
        } else {
            this.successors = Arrays.stream(successors)
                    .filter(s -> s != null)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Prints the information of the current state.
     */
    protected abstract void printInformation();

    /**
     * Prints the possible choices for transitions
     */
    protected void printTransitionChoices() {
        String transitionChoices = IntStream.range(0, successors.size())
                .mapToObj(i -> successors.get(i).asDisplayString(i + ") "))
                .collect(Collectors.joining("\n"));
        System.out.println("Next step:\n" + transitionChoices);
    }

    /**
     * Parses the given choices for the transition
     */
    protected void parseTransitionChoice() { // TODO move to subclass and add parameter
        this.transitionChoice = parseUserChoice(successors);
    }

    /**
     * Parses the user input for the choice of the next action<br>
     * We store actions in lists and print before the choice. So the index is used
     * to determine the next action.
     *
     * @param choices the possible choices
     * @return the choice of the user
     */
    protected List<Integer> parseUserChoices(final List<?> choices) {
        return IOUtils.readAndParseChoicesFromUser(choices);
    }


    /**
     * Parses the user input for the choice of the next action<br>
     * We store actions in lists and print before the choice. So the index is used
     * to determine the next action.
     *
     * @param choices the possible choices
     * @return the choice of the user
     */
    protected int parseUserChoice(final List<?> choices) {
        return IOUtils.readAndParseSingleChoiceFromUser(choices);
    }

    /**
     * Performs the transition to the next state
     */
    protected void transition() {
        stateMachine.setState(successors.get(this.transitionChoice));
    }

    /**
     * Returns a string for displaying the information of the state
     * on the command line
     *
     * @param prefix an optional prefix
     * @return the string
     */
    public String asDisplayString(final String prefix) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null && !prefix.isEmpty()) sb.append(prefix);
        sb.append(this.getClass().getSimpleName().replace("State", ""));
        return sb.toString();
    }

    //////////////////////////////////
    /////////// EXECUTIONS ///////////
    //////////////////////////////////
    /**
     * Collects data for the execution. Usually these are refId of object to perform actions on later.
     */
    protected void collectDataForExecution() {
    }

    /**
     * Prints the possible execution choices for the state.
     */
    protected void printExecutionChoices() {
    }

    /**
     * Parses the given choices for execution.
     */
    protected void parseExecutionChoices() {
    }

    /**
     * Prints a small summary of the amount of affected nodes/objects of the execution
     */
    protected void printExecutionPreview() {
    }

    /**
     * Prints and requests a confirmation from the user for the upcoming action.<br>
     * Does <b>NOT</b> change the context.
     */
    protected boolean confirm() {
        return true;
    }

    /**
     * Executes the command of the state.
     */
    protected void execute() {
    }

    /**
     * Prints the summary of the preceding execution.
     */
    protected void printExecutionSummary() {
    }
}
