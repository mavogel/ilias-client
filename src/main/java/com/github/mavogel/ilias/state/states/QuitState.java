package com.github.mavogel.ilias.state.states;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

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
        ILIASSoapWebservicePortType endPoint = this.stateMachine.getEndPoint();
        String sid = this.stateMachine.getUserDataIds().getSid();
        try {
            endPoint.logout(sid);
        } catch (RemoteException e) {
            System.err.println("Could not log out: " + e.getMessage());
        } finally {
            this.stateMachine.stop();
        }
        return "";
    }
}
