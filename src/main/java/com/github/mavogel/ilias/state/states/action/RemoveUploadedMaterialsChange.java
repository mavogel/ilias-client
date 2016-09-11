package com.github.mavogel.ilias.state.states.action;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.state.ChangeAction;
import com.github.mavogel.ilias.utils.IOUtils;
import com.github.mavogel.ilias.utils.IliasUtils;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.List;

/**
 * Created by mavogel on 9/9/16.
 */
public class RemoveUploadedMaterialsChange implements ChangeAction {

    @Override
    public String performAction(final ILIASSoapWebservicePortType endpoint, final UserDataIds userDataIds,
                                final List<IliasNode> nodes) {
        System.out.println("Removing uploaded materials from groups");
        confirm();

        final String sid = userDataIds.getSid();
        final int userId = userDataIds.getUserId();
        List<IliasNode> fileNodes = null;
        try {
            fileNodes = IliasUtils.retrieveFileRefIdsFromGroups(endpoint, sid, userId, nodes);
            IliasUtils.deleteObjects(endpoint, sid, fileNodes); // TODO return summary
        } catch (IOException | JDOMException e) {
            System.err.println("Error creating xml parser: " + e.getMessage());
        }
        return "";
    }

    @Override
    public boolean confirm() {
        return IOUtils.readAndParseUserConfirmation();
    }
}
