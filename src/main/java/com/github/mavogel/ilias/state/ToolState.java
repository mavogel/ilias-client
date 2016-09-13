package com.github.mavogel.ilias.state;

import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.state.states.LoginState;
import com.github.mavogel.ilias.utils.IOUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mavogel on 9/6/16.
 */
public abstract class ToolState {

    private static Logger LOG = Logger.getLogger(ToolState.class);

    protected ToolStateMachine stateMachine;
    protected List<ToolState> successors;

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
     * member variable.
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
     * Prints and parses the possible choices for transitions
     */
    protected int printAndParseTransitionChoices() {
        if (!successors.isEmpty()) {
            LOG.info("Next step:");
            IntStream.range(0, successors.size())
                    .mapToObj(i -> successors.get(i).asDisplayString(i + ") "))
                    .forEach(LOG::info);
            return IOUtils.readAndParseSingleChoiceFromUser(successors);
        } else {
            return -1;
        }
    }

    /**
     * Performs the transition to the next state
     */
    protected void transition() {
        if(!successors.isEmpty()) {
            final int transitionChoice = printAndParseTransitionChoices();
            stateMachine.setState(successors.get(transitionChoice));
        }
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
     *
     * @return the nodes for the execution
     */
    protected abstract List<IliasNode> collectDataForExecution();

    /**
     * Parses the given choices for execution.
     *
     * @param nodeChoices the node the user can choose from
     * @return the chosen nodes with the desired actions to perform
     */
    protected abstract IliasAction printAndParseExecutionChoices(List<IliasNode> nodeChoices);

    /**
     * Executes the command of the state.
     */
    public void execute() {
        List<IliasNode> nodeChoices = this.collectDataForExecution();
        IliasAction chosenNodesAndActions = this.printAndParseExecutionChoices(nodeChoices);
        String summary = this.doExecute(chosenNodesAndActions);
        printExecutionSummary(summary);
    }

    /**
     * Internal execute
     *
     * @param nodesAndActions the nodes and actions
     * @return the summary of the actions
     */
    protected abstract String doExecute(final IliasAction nodesAndActions);

    /**
     * Prints the summary of the preceding execution.
     */
    protected void printExecutionSummary(final String summary) {
        if (summary != null && !summary.isEmpty()) LOG.info(summary);
        LOG.info("--------------------------------");
    }
}
