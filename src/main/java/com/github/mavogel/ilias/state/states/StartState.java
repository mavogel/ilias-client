package com.github.mavogel.ilias.state.states;

import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;

/**
 * Created by mavogel on 9/6/16.
 */
public class StartState implements ToolState {

    private ToolStateMachine stateMachine;

    public StartState(final ToolStateMachine stateMachine) {
        this.stateMachine = stateMachine;
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

    @Override
    public void printExecutionChoices() {
    }

    @Override
    public void printExecutionPreview() {
    }

    @Override
    public void parseExecutionChoices() {
    }

    @Override
    public boolean confirm() {
        return true;
    }

    @Override
    public void execute() {
    }

    @Override
    public void printExectionSummary() {
    }


    @Override
    public void printTransitionChoices() {
    }

    @Override
    public void parseTransitionChoice() {
    }

    @Override
    public void transition() {
        stateMachine.setState(stateMachine.getChooseCoursesState());
    }

    @Override
    public String asDisplayString(final String prefix) {
        return "";
    }
}
