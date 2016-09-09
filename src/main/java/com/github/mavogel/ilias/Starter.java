package com.github.mavogel.ilias;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.ConfigurationsUtils;
import com.github.mavogel.ilias.utils.IliasUtils;
import org.apache.commons.lang3.Validate;

/**
 * Created by mavogel on 8/29/16.
 */
public class Starter {

    public static void main(String[] args) {
        Validate.notNull(args, "No arguments given");
        Validate.isTrue(args.length == 1, "Only one argument is allowed. The 'config.properties'");
        Validate.isTrue(!args[0].isEmpty(), "The argument is empty");
        createEndpointAndRun(ConfigurationsUtils.createLoginConfiguration(args[0]));
    }

    /**
     * Create the endpoint and starts the State machine with the given login configuration.
     *
     * @param loginConfiguration the login configuration
     */
    private static void createEndpointAndRun(LoginConfiguration loginConfiguration) {
        try {
            ILIASSoapWebservicePortType endpoint = IliasUtils.createWsEndpoint(loginConfiguration);
            ToolStateMachine stateMachine = new ToolStateMachine(loginConfiguration);
            stateMachine.start();
        } catch (javax.xml.rpc.ServiceException ex) {
            System.err.println("Could not create ws endpoint at '" + loginConfiguration.getEndpoint() + "'. Check you internet connection");
        }
    }
}
