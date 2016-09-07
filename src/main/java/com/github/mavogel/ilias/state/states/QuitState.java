package com.github.mavogel.ilias.state.states;

import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;

/**
 * Created by mavogel on 9/7/16.
 */
public class QuitState extends ToolState {

    public QuitState(final ToolStateMachine stateMachine) {
        super(stateMachine);
    }

    @Override
    public void printInformation() {
        System.out.println("Quitting... bye bye!");
        // TODO logout
        this.stateMachine.stop();
    }
}
