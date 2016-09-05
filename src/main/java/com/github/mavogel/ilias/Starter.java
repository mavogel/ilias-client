package com.github.mavogel.ilias;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.utils.ConfigurationsUtils;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.utils.IliasUtils;
import com.github.mavogel.ilias.utils.XMLUtils;
import org.apache.commons.lang3.Validate;
import org.jdom.JDOMException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.rmi.RemoteException;
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
            endpoint = IliasUtils.createWsEndpoint(loginConfiguration);
            UserDataIds userData = IliasUtils.getUserData(loginConfiguration, endpoint);
            int userId = userData.getUserId();
            sid = userData.getSid();

            // 3: workflow start
            String selectedCourses = endpoint.getCoursesForUser(sid,
                    XMLUtils.createCoursesResultXml(userId, IliasUtils.DisplayStatus.ADMIN));
            System.out.printf("courses for user: %s%n", selectedCourses);
            List<Integer> courseRefIds = XMLUtils.parseCourseRefIds(selectedCourses);

            // 3.1 each course
            List<Integer> groupRefIds = IliasUtils.retrieveGroupRefIdsFromCourses(endpoint, sid, userId, courseRefIds, 5);

//            // 3.2 each folder
//            ref_id first gr
//            int ref_id_first_group = 146443;
//            String treeChildsOfGroup = endpoint.getTreeChilds(sid, ref_id_first_group, new String[]{"file"}, userId);
//            System.out.printf("treeChildsOfGroup: %s%n", treeChildsOfGroup); // gives me the files, type 'file'
//            String groupInfo = endpoint.getGroup(sid, ref_id_first_group);
//            System.out.printf("GroupInfo: %s%n", groupInfo);
//            System.out.println("Seconds: " + Instant.ofEpochSecond(1458568800));

            // Updates:
            // 1: remove users: √
//            int user_id = 184191;
//            boolean groupMemberExcluded = endpoint.excludeGroupMember(sid, ref_id_first_group, user_id);
//            System.out.println("excluded Member with id:" + user_id + " -> " + groupMemberExcluded);
            // 2: set registration period √
//            LocalDateTime newStartDate = LocalDateTime.of(2016, Month.AUGUST, 22, 14, 00);
//            long newStart = newStartDate.toEpochSecond(ZoneOffset.UTC); // TODO
//            long newEnd = newStartDate.plusDays(2).toEpochSecond(ZoneOffset.UTC);
//            System.out.println("new: " + newStart + " -> " + Instant.ofEpochSecond(newStart));
//            String groupUpdate = "<group><title>Group A01</title><description>Usersetting: dbex01</description><owner id=\"il_0_usr_183084\"/><information/><registration type=\"direct\" waitingList=\"No\"><temporarilyAvailable><start>" + newStart +"</start><end>" + newEnd + "</end></temporarilyAvailable><maxMembers enabled=\"Yes\">3</maxMembers></registration><admin id=\"il_0_usr_183084\" notification=\"Yes\"/><member id=\"il_0_usr_184191\"/><member id=\"il_0_usr_206011\"/><member id=\"il_0_usr_206074\"/><Sort type=\"Inherit\"/><ContainerSettings><ContainerSetting id=\"cont_auto_rate_new_obj\">0</ContainerSetting><ContainerSetting id=\"cont_show_calendar\">1</ContainerSetting><ContainerSetting id=\"cont_show_news\">1</ContainerSetting><ContainerSetting id=\"cont_tag_cloud\">0</ContainerSetting></ContainerSettings></group>";
//            boolean isGroupUpdated = endpoint.updateGroup(sid, ref_id_first_group, groupUpdate);
//            System.out.println("Group updated?: " + isGroupUpdated);
            // 3: remove uploaded materials √
//            boolean objectDeleted = endpoint.deleteObject(sid, 203095);
//            System.out.printf("objectDeleted: %s%n", objectDeleted);

        } catch (javax.xml.rpc.ServiceException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        } catch (RemoteException ex) {
            // auth failed comes here
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
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
