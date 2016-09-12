package com.github.mavogel.ilias.state.states;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.IliasUtils;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

/**
 * Created by mavogel on 9/7/16.
 */
public class LoginState extends ToolState {

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
        System.out.println("Logging in and getting user data...");
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
        } catch (ServiceException e) {
            System.err.println("Error creating the endpoint: " + e.getMessage());
            stateMachine.setState(stateMachine.getQuitState());
        } catch (RemoteException e) {
            System.err.println("Error retrieving the user data: " + e.getMessage());
            stateMachine.setState(stateMachine.getQuitState());
        }
        return String.format("Logged in successfully as '%s'", loginConfiguration.getUsername());
    }
}
