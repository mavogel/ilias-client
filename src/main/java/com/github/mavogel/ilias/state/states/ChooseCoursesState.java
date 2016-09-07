package com.github.mavogel.ilias.state.states;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.IliasUtils;
import com.github.mavogel.ilias.utils.XMLUtils;
import org.jdom.JDOMException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mavogel on 9/7/16.
 */
public class ChooseCoursesState extends ToolState {

    private List<IliasNode> coursesForUser;
    private List<Integer> indexesOfChosenCourses;

    public ChooseCoursesState(final ToolStateMachine stateMachine, final ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    public void printInformation() {
        System.out.println("Choose a topic");
    }

    @Override
    protected void collectDataForExecution() {
        ILIASSoapWebservicePortType endpoint = stateMachine.getEndPoint();
        String sid = stateMachine.getUserDataIds().getSid();
        int userId = stateMachine.getUserDataIds().getUserId();

        try {
            this.coursesForUser = IliasUtils.getCoursesForUser(endpoint, sid, userId, IliasUtils.DisplayStatus.ADMIN);
        } catch (RemoteException e) {
            System.err.println("Could not retrieve courses for user : " + e.getMessage());
            this.stateMachine.setState(stateMachine.getQuitState());
        } catch (JDOMException | IOException e) {
            System.err.println("Error creating xml parser: " + e.getMessage());
            this.stateMachine.setState(stateMachine.getQuitState());
        }
    }

    @Override
    protected void printExecutionChoices() {
        String executionChoices = IntStream.range(0, this.coursesForUser.size())
                .mapToObj(i -> this.coursesForUser.get(i).asDisplayString(i + ") "))
                .collect(Collectors.joining("\n"));
        System.out.println(executionChoices);
    }

    @Override
    protected void parseExecutionChoices() {
        this.indexesOfChosenCourses = super.parseUserChoices(this.coursesForUser);
    }

    @Override
    protected void execute() {
        stateMachine.getContext().put(ToolStateMachine.ContextKey.COURSES,
                this.indexesOfChosenCourses.stream()
                        .map(idx -> this.coursesForUser.get(idx))
                        .collect(Collectors.toList()));
    }
}
