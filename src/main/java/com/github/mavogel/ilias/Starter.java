package com.github.mavogel.ilias;

import com.github.mavogel.client.ILIASSoapWebserviceLocator;
import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.client.IlUserData;
import com.github.mavogel.ilias.utils.ConfigurationsUtils;
import com.github.mavogel.ilias.utils.LoginConfiguration;
import org.apache.commons.lang3.Validate;

import java.rmi.RemoteException;
import java.time.*;

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
            ILIASSoapWebserviceLocator locator = new ILIASSoapWebserviceLocator();
            locator.setILIASSoapWebservicePortEndpointAddress(loginConfiguration.getEndpoint());

            endpoint = locator.getILIASSoapWebservicePort();
            sid = endpoint.loginLDAP(loginConfiguration.getClient(), loginConfiguration.getUsername(), loginConfiguration.getPassword());
            System.out.printf("sid: %s%n", sid);
            int userIdBySid = endpoint.getUserIdBySid(sid);
            System.out.printf("user_id by sid: %d%n", userIdBySid);
            String coursesForUser = endpoint.getCoursesForUser(sid, "<result><colspecs><colspec name=\"user_id\"/><colspec name=\"status\"/></colspecs><row><column>" + userIdBySid + "</column><column>4</column></row></result>");
            System.out.printf("courses for user: %s%n", coursesForUser);
            // TODO parse course xmls
            int course_ref_id = 44525;
            String courseXML = endpoint.getCourseXML(sid, course_ref_id); // from column 1
            System.out.printf("courseXML: %s%n", courseXML);
            // Group folder : https://mycompany.com/ilias.php?ref_id=44528
            String treeChilds = endpoint.getTreeChilds(sid, course_ref_id, new String[]{"grp", "fold"}, userIdBySid);
            System.out.printf("TreeChilds:%s%n", treeChilds);
            // parse them for type 'grp' and type 'fold' and get their ref_ids
            final int folder_ref_id = 44528;
            String treeChildsFolder = endpoint.getTreeChilds(sid, folder_ref_id, new String[]{"grp", "fold"}, userIdBySid);
            System.out.printf("TreeChild for folder:%s%n", treeChildsFolder);
            // ref_id first gr
            int ref_id_first_group = 146443;
            String treeChildsOfGroup = endpoint.getTreeChilds(sid, ref_id_first_group, new String[]{"file"}, userIdBySid);
            System.out.printf("treeChildsOfGroup: %s%n", treeChildsOfGroup); // gives me the files, type 'file'
            String groupInfo = endpoint.getGroup(sid, ref_id_first_group);
            System.out.printf("GroupInfo: %s%n", groupInfo);
            System.out.println("Seconds: " + Instant.ofEpochSecond(1458568800));

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
        } catch (RemoteException ex) {
            // auth failed comes here
            System.err.println(ex.getMessage());
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
