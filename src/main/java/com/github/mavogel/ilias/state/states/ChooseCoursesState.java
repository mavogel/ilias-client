package com.github.mavogel.ilias.state.states;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.IliasUtils;
import com.github.mavogel.ilias.utils.XMLUtils;
import org.jdom.JDOMException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

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
        System.out.println("Choose a topic");
    }

    @Override
    protected void collectDataForExecution() {
        ILIASSoapWebservicePortType endpoint = stateMachine.getEndPoint();
        String sid = stateMachine.getUserDataIds().getSid();
        int userId = stateMachine.getUserDataIds().getUserId();

        try {
            String selectedCourses = endpoint.getCoursesForUser(sid,
                    XMLUtils.createCoursesResultXml(userId, IliasUtils.DisplayStatus.ADMIN));
            stateMachine.getContext().put(ToolStateMachine.ContextKey.COURSES,
                                          XMLUtils.parseCourseRefIds(selectedCourses)); // TODO make node info model
        } catch (RemoteException e) {
            System.err.println("Could not retrieve courses for user : " + e.getMessage());
            this.stateMachine.setState(stateMachine.getQuitState());
        } catch (JDOMException | IOException e) {
            System.err.println("Error creating xml parser: " + e.getMessage());
            this.stateMachine.setState(stateMachine.getQuitState());
        }
    }

    // GO on
}
