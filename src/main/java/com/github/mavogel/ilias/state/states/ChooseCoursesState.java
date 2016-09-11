package com.github.mavogel.ilias.state.states;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.state.ChangeAction;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.state.states.action.RemoveUploadedMaterialsChange;
import com.github.mavogel.ilias.utils.IOUtils;
import com.github.mavogel.ilias.utils.IliasUtils;
import com.github.mavogel.ilias.utils.XMLUtils;
import org.jdom.JDOMException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mavogel on 9/7/16.
 */
public class ChooseCoursesState extends ToolState {

    public ChooseCoursesState(final ToolStateMachine stateMachine, final ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    public void printInformation() {
        System.out.println("Choose a Course");
        this.stateMachine.getContext().remove(ToolStateMachine.ContextKey.GROUPS);
    }

    @Override
    protected List<IliasNode> collectDataForExecution() {
        final ILIASSoapWebservicePortType endpoint = stateMachine.getEndPoint();
        final String sid = stateMachine.getUserDataIds().getSid();
        final int userId = stateMachine.getUserDataIds().getUserId();

        try {
            return IliasUtils.getCoursesForUser(endpoint, sid, userId, IliasUtils.DisplayStatus.ADMIN);
        } catch (RemoteException e) {
            System.err.println("Could not retrieve courses for user : " + e.getMessage());
            this.stateMachine.setState(stateMachine.getQuitState());
        } catch (JDOMException | IOException e) {
            System.err.println("Error creating xml parser: " + e.getMessage());
            this.stateMachine.setState(stateMachine.getQuitState());
        }
        return Collections.emptyList();
    }

    @Override
    protected IliasAction printAndParseExecutionChoices(final List<IliasNode> nodeChoices) {
        IntStream.range(0, nodeChoices.size())
                .mapToObj(i -> nodeChoices.get(i).asDisplayString(i + ") "))
                .forEach(System.out::println);

        List<Integer> indexesOfChosenNodes = IOUtils.readAndParseChoicesFromUser(nodeChoices);
        List<IliasNode> choseIliasNodes = indexesOfChosenNodes.stream()
                .map(idx -> nodeChoices.get(idx))
                .collect(Collectors.toList());

        return new IliasAction(choseIliasNodes, null);
    }

    @Override
    protected String doExecute(final IliasAction nodesAndActions) {
        stateMachine.getContext().put(ToolStateMachine.ContextKey.COURSES, nodesAndActions.getNodes());
        return "";
    }
}
