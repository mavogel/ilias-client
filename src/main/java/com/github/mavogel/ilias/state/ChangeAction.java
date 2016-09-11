package com.github.mavogel.ilias.state;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.UserDataIds;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.List;

/**
 * Created by mavogel on 9/7/16.
 */
public interface ChangeAction {

    /**
     * Performs the action of the state.
     *
     * @param endpoint    the endpoint of the Webservice
     * @param userDataIds the ids of the users
     * @param nodes       the nodes to perform the action on
     * @return the summary of the performed action
     */
    String performAction(final ILIASSoapWebservicePortType endpoint, final UserDataIds userDataIds,
                         final List<IliasNode> nodes);

    /**
     * Prints and requests a confirmation from the user for the upcoming action.<br>
     * Does <b>NOT</b> change the context.
     *
     * @return <code>true</code> for as yes input, <code>false</code> otherwise
     */
    boolean confirm();

    /**
     * The name of the action
     *
     * @param prefix an optional prefix
     * @return its name
     */
    String actionName(final String prefix);
}
