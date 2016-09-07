package com.github.mavogel.ilias.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mavogel on 9/6/16.
 */
public abstract class ToolState {

    protected ToolStateMachine stateMachine;
    protected List<ToolState> successors;

    private int transitionChoice;

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
        if (successors == null || successors.length == 1) {
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
                .mapToObj(i -> successors.get(i).asDisplayString(i + 1 + ") "))
                .collect(Collectors.joining("\n"));
        System.out.println(transitionChoices);
    }

    /**
     * Parses the given choices for the transition
     */
    protected void parseTransitionChoice() {
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
    protected int parseUserChoice(final List<?> choices) {
        boolean isCorrectInput = false;
        String line = null;
        int userChoice = -1;

        while (!isCorrectInput) {
            try (Scanner scanner = new Scanner(System.in)) {
                line = scanner.nextLine();
                userChoice = Integer.valueOf(line).intValue();
                isCorrectInput = isInRange(choices, userChoice);
            } catch (NumberFormatException nfe) {
                System.err.println("'" + line + " is not a number! Try again");
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        return userChoice;
    }

    /**
     * Checks if the given index is in the range of the list
     *
     * @param list  the list
     * @param index the index to check
     * @return <code>true</code> if the indes is in the range of the list
     * @throws IllegalArgumentException if the index is not in the range.
     */
    private boolean isInRange(final List<?> list, final int index) {
        if (index >= 0 && index < list.size()) {
            return true;
        } else {
            throw new IllegalArgumentException("Choice '" + index + "' is not in range! Try again");
        }
    }

    /**
     * Performs the transition to the next state
     */
    protected void transition() {
        stateMachine.setState(successors.get(this.transitionChoice - 1));
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

    /////////// EXECUTIONS ///////////

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
     * Prints a small summary of the amount of affected nodes/objects of the execution
     */
    protected void printExecutionPreview() {
    }

    /**
     * Prints and requests a confirmation from the user for the upcoming action.
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

    /**
     * Parses the given choices for execution.
     */
    protected void parseExecutionChoices() {
    }
}
