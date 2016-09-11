package com.github.mavogel.ilias.state.states.action;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.state.ExecutionState;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.IOUtils;
import com.github.mavogel.ilias.utils.IliasUtils;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.List;

/**
 * Created by mavogel on 9/9/16.
 */
public class RemoveUploadedMaterialsState extends ToolState implements ExecutionState {


    private List<IliasNode> selectedGroups;

    /**
     * C'tor for a {@link ToolState}
     *
     * @param stateMachine the {@link ToolStateMachine}
     * @param successors   the successor states
     */
    public RemoveUploadedMaterialsState(final ToolStateMachine stateMachine, final ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    protected void printInformation() {
        System.out.println("Removing uploaded materials from groups");
    }

    @Override
    protected void collectDataForExecution() {
        this.selectedGroups = stateMachine.getContext().get(ToolStateMachine.ContextKey.GROUPS);
    }

    @Override
    protected void printExecutionPreview() {
        System.out.println("Will remove users of " + selectedGroups.size() + " groups!");
    }

    @Override
    protected boolean confirm() {
        return IOUtils.readAndParseUserConfirmation();
    }

    @Override
    protected void execute() {
        ILIASSoapWebservicePortType endpoint = stateMachine.getEndPoint();
        final String sid = stateMachine.getUserDataIds().getSid();
        final int userId = stateMachine.getUserDataIds().getUserId();
        try {
            List<IliasNode> fileNodes = IliasUtils.retrieveFileRefIdsFromGroups(endpoint, sid, userId, selectedGroups);
            IliasUtils.deleteObjects(endpoint, sid, fileNodes);
        } catch (IOException | JDOMException e) {
            System.err.println("Error creating xml parser: " + e.getMessage());
            this.stateMachine.setState(stateMachine.getQuitState());
        }
    }

    @Override
    protected void printExecutionSummary() {
        // done internally in IliasUtils.removeAllMembersFromGroups
    }
}
