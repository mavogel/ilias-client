package com.github.mavogel.ilias.state.states;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.IliasUtils;
import org.apache.log4j.Logger;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

/**
 * Created by mavogel on 9/7/16.
 */
public class LoginState extends ToolState {

    private static Logger LOG = Logger.getLogger(LoginState.class);

    private LoginConfiguration loginConfiguration;

    public LoginState(final ToolStateMachine stateMachine,
                      final LoginConfiguration loginConfiguration,
                      final ToolState... successors) {
        super(stateMachine);
        this.loginConfiguration = loginConfiguration;
        setSuccessors(successors);
    }

    @Override
    protected void printInformation() {
        LOG.info("Logging in and getting user data...");
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
        try {
            ILIASSoapWebservicePortType endpoint = IliasUtils.createWsEndpoint(loginConfiguration);
            stateMachine.setEndPoint(endpoint);
            stateMachine.setUserDataId(IliasUtils.getUserData(loginConfiguration, endpoint));
            return String.format("Logged in successfully as '%s'", loginConfiguration.getUsername());
        } catch (ServiceException e) {
            LOG.error("Could not establish webservice at '" + loginConfiguration.getEndpoint() + "'");
            stateMachine.setState(stateMachine.getQuitState());
            return "";
        } catch (RemoteException e) {
            Throwable cause = e.getCause();
            if (cause instanceof UnknownHostException) {
                LOG.error("Could not establish connection to endpoint '" + loginConfiguration.getEndpoint()+ "'");
            } else {
                LOG.error("Error retrieving the user data: " + e.getMessage());
            }
            stateMachine.setState(stateMachine.getQuitState());
            return "";
        }
    }
}
