package com.github.mavogel.ilias.state.states;

import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;

import java.util.Arrays;
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
        sb.append(" _______ __                        ")
          .append("|       |  |--.-----.              ")
          .append("|.|   | |     |  -__|              ")
          .append("`-|.  |-|__|__|_____|              ")
          .append("  |:  |                            ")
          .append("  |::.|                            ")
          .append("  `---'                            ")
          .append(" ___ ___     ___ _______ _______   ")
          .append("|   |   |   |   |   _   |   _   |  ")
          .append("|.  |.  |   |.  |.  1   |   1___|  ")
          .append("|.  |.  |___|.  |.  _   |____   |  ")
          .append("|:  |:  1   |:  |:  |   |:  1   |  ")
          .append("|::.|::.. . |::.|::.|:. |::.. . |  ")
          .append("`---`-------`---`--- ---`-------'  ")
          .append(" _______ __ __             __      ")
          .append("|   _   |  |__.-----.-----|  |_    ")
          .append("|.  1___|  |  |  -__|     |   _|   ")
          .append("|.  |___|__|__|_____|__|__|____|   ")
          .append("|:  1   |                          ")
          .append("|::.. . |                          ")
          .append("`-------'                          ");
        System.out.println(sb.toString());

    }
}
