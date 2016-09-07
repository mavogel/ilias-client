package com.github.mavogel.ilias;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.ConfigurationsUtils;
import com.github.mavogel.ilias.utils.IliasUtils;
import com.github.mavogel.ilias.utils.XMLUtils;
import org.apache.commons.lang3.Validate;
import org.jdom.JDOMException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by mavogel on 8/29/16.
 */
public class Starter {

    public static void main(String[] args) {
        Validate.notNull(args, "No arguments given");
        Validate.isTrue(args.length == 1, "Only one argument is allowed. The 'config.properties'");
        Validate.isTrue(!args[0].isEmpty(), "The argument is empty");
        run(ConfigurationsUtils.createLoginConfiguration(args[0]));
    }

    private static void run(LoginConfiguration loginConfiguration) {
        ILIASSoapWebservicePortType endpoint = null;
        String sid = "";
        try {
            // LoginState √
            endpoint = IliasUtils.createWsEndpoint(loginConfiguration);
            UserDataIds userData = IliasUtils.getUserData(loginConfiguration, endpoint);
            int userId = userData.getUserId();
            sid = userData.getSid();

            String courseXML = endpoint.getCourseXML(sid, 44525);
            System.out.println("Course xml: " + courseXML);


            if (false) {
                // 3: workflow start
                ToolStateMachine stateMachine = new ToolStateMachine(loginConfiguration);
                stateMachine.start();

                // ChooseCoursesState √
                String selectedCourses = endpoint.getCoursesForUser(sid,
                        XMLUtils.createCoursesResultXml(userId, IliasUtils.DisplayStatus.ADMIN));
                System.out.printf("courses for user: %s%n", selectedCourses);
                List<Integer> courseRefIds = XMLUtils.parseCourseRefIds(selectedCourses);

                // 3.1 each course
                int maxFolderDepth = 5; // into config file
                List<Integer> groupRefIds = IliasUtils.retrieveGroupRefIdsFromCourses(endpoint, sid, userId,
                                                                                      courseRefIds, maxFolderDepth);

                // Updates:
                // 1: remove users: √
                IliasUtils.removeAllMembersFromGroups(endpoint, sid, groupRefIds);

                // 2: set registration period √
//            TODO
                LocalDateTime registrationStart = LocalDateTime.parse("", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                LocalDateTime registrationEnd = LocalDateTime.parse("", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                IliasUtils.setRegistrationDatesOnGroupes(endpoint, sid, groupRefIds, registrationStart, registrationEnd);

                // 3: remove uploaded materials √
                List<Integer> fileRefIds = IliasUtils.retrieveFileRefIdsFromGroups(endpoint, sid, userId, groupRefIds);
                IliasUtils.deleteObjects(endpoint, sid, fileRefIds);
            }

        } catch (javax.xml.rpc.ServiceException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        } catch (RemoteException ex) {
            // auth failed comes here
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        } finally {
            if (endpoint != null) {
                try {
                    endpoint.logout(sid);
                    System.out.println("--> Logout");
                } catch (RemoteException e) {
                    // just do it
                }
            }
        }
    }
}
