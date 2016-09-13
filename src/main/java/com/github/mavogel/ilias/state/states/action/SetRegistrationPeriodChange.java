package com.github.mavogel.ilias.state.states.action;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.RegistrationPeriod;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.state.ChangeAction;
import com.github.mavogel.ilias.utils.IOUtils;
import com.github.mavogel.ilias.utils.IliasUtils;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.List;

/**
 * Created by mavogel on 9/9/16.
 */
public class SetRegistrationPeriodChange implements ChangeAction {

    private static Logger LOG = Logger.getLogger(SetRegistrationPeriodChange.class);

    @Override
    public String performAction(final ILIASSoapWebservicePortType endpoint, final UserDataIds userDataIds,
                                final List<IliasNode> nodes) {
        LOG.info("Setting registration date");
        RegistrationPeriod registrationPeriod = IOUtils.readAndParseRegistrationDates();
        confirm();

        final String sid = userDataIds.getSid();
        try {
            IliasUtils.setRegistrationDatesOnGroups(endpoint, sid, nodes,
                    registrationPeriod.getRegistrationStart(), registrationPeriod.getRegistrationEnd());
        } catch (IOException | JDOMException e) {
            LOG.error("Error creating xml parser: " + e.getMessage());
        }
        return "";
    }

    @Override
    public boolean confirm() {
        return IOUtils.readAndParseUserConfirmation();
    }
}
