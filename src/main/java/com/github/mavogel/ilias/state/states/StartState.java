package com.github.mavogel.ilias.state.states;

import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mavogel on 9/6/16.
 */
public class StartState extends ToolState {

    public StartState(final ToolStateMachine stateMachine, ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    public void printInformation() {
        StringBuilder sb = new StringBuilder();
        sb.append(" _______ __                        ").append("\n")
          .append("|       |  |--.-----.              ").append("\n")
          .append("|.|   | |     |  -__|              ").append("\n")
          .append("`-|.  |-|__|__|_____|              ").append("\n")
          .append("  |:  |                            ").append("\n")
          .append("  |::.|                            ").append("\n")
          .append("  `---'                            ").append("\n")
          .append(" ___ ___     ___ _______ _______   ").append("\n")
          .append("|   |   |   |   |   _   |   _   |  ").append("\n")
          .append("|.  |.  |   |.  |.  1   |   1___|  ").append("\n")
          .append("|.  |.  |___|.  |.  _   |____   |  ").append("\n")
          .append("|:  |:  1   |:  |:  |   |:  1   |  ").append("\n")
          .append("|::.|::.. . |::.|::.|:. |::.. . |  ").append("\n")
          .append("`---`-------`---`--- ---`-------'  ").append("\n")
          .append(" _______ __ __             __      ").append("\n")
          .append("|   _   |  |__.-----.-----|  |_    ").append("\n")
          .append("|.  1___|  |  |  -__|     |   _|   ").append("\n")
          .append("|.  |___|__|__|_____|__|__|____|   ").append("\n")
          .append("|:  1   |                          ").append("\n")
          .append("|::.. . |                          ").append("\n")
          .append("`-------'                          ").append("\n");
        System.out.println(sb.toString());

    }

    @Override
    protected int printAndParseTransitionChoices() {
        return 0;
    }

    @Override
    protected List<IliasNode> collectDataForExecution() {
        return Collections.emptyList();
    }

    @Override
    protected IliasAction printAndParseExecutionChoices(final List<IliasNode> nodeChoices) {
        return new IliasAction();
    }

    @Override
    protected String doExecute(final IliasAction nodesAndActions) {
        return "";
    }
}
