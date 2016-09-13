package com.github.mavogel.ilias.state.states;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

/**
 * Created by mavogel on 9/7/16.
 */
public class QuitState extends ToolState {

    private static Logger LOG = Logger.getLogger(QuitState.class);

    public QuitState(final ToolStateMachine stateMachine) {
        super(stateMachine);
        this.setSuccessors();
    }

    @Override
    public void printInformation() {
        LOG.info("Quitting... bye bye!");
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
        UserDataIds userDataIds = this.stateMachine.getUserDataIds();
        if (userDataIds != null && endPoint != null) {
            try {
                boolean isLoggedOut = endPoint.logout(userDataIds.getSid());
                if (isLoggedOut) {
                    LOG.info("Successfully logged out for sid: '" + userDataIds.getSid() + "'");
                } else {
                    LOG.error("Could not log out!");
                }
            } catch (RemoteException e) {
                LOG.error("Could not log out: " + e.getMessage());
            }
        }
        this.stateMachine.stop();
        return "";
    }
}
