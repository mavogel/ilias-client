package com.github.mavogel.ilias.utils;

import com.github.mavogel.client.ILIASSoapWebserviceLocator;
import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;
import org.jdom.JDOMException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mavogel on 9/5/16.
 */
public class IliasUtils {

    /**
     * The DisplayStatus has four values:
     * <ul>
     * <li>MEMBER = 1</li>
     * <li>TUTOR = 2</li>
     * <li>ADMIN = 4</li>
     * <li>OWNER = 8</li>
     * </ul>
     * and determines which courses should be returned.
     * <p>
     * It can be used for example in
     * {@link com.github.mavogel.client.ILIASSoapWebservicePortType#getCoursesForUser(String, String)}
     */
    public enum DisplayStatus {

        MEMBER(1),
        TUTOR(2),
        ADMIN(4),
        OWNER(8);

        private final int statusNumber;

        DisplayStatus(final int statusNumber) {
            this.statusNumber = statusNumber;
        }

        /**
         * @return the number of the statusNumber
         */
        public int asNumber() {
            return statusNumber;
        }
    }

    /**
     * Creates the endpoint of the ilias soap interface.
     *
     * @param loginConfiguration the config of the login
     * @return the endpoint as {@link ILIASSoapWebservicePortType}
     * @throws javax.xml.rpc.ServiceException
     */
    public static ILIASSoapWebservicePortType createWsEndpoint(final LoginConfiguration loginConfiguration) throws javax.xml.rpc.ServiceException {
        ILIASSoapWebserviceLocator locator = new ILIASSoapWebserviceLocator();
        locator.setILIASSoapWebservicePortEndpointAddress(loginConfiguration.getEndpoint());
        return locator.getILIASSoapWebservicePort();
    }

    /**
     * Retrieves the information about the user.
     *
     * @param loginConfiguration the config for the logon
     * @param endpoint           the ilias endpoint
     * @return the user data as {@link UserDataIds}
     * @throws RemoteException
     */
    public static UserDataIds getUserData(final LoginConfiguration loginConfiguration,
                                          final ILIASSoapWebservicePortType endpoint) throws RemoteException {
        String sid = "";
        switch (loginConfiguration.getLoginMode()) {
            case LDAP:
                sid = endpoint.loginLDAP(loginConfiguration.getClient(),
                        loginConfiguration.getUsername(), loginConfiguration.getPassword());
                break;
            case STD:
                sid = endpoint.login(loginConfiguration.getClient(),
                        loginConfiguration.getUsername(), loginConfiguration.getPassword());
                break;
            case CAS:
                throw new UnsupportedOperationException("login with CAS is not yet supported");
        }
        System.out.printf("sid: %s%n", sid);
        int userId = endpoint.getUserIdBySid(sid);
        System.out.printf("user_id by sid: %d%n", userId);
        return new UserDataIds(userId, sid);

    }

    /**
     * Retrieves the course of a user in which he has the given {@link DisplayStatus}.
     *
     * @param endpoint the {@link ILIASSoapWebservicePortType}
     * @param sid      the sid of the user obtained at the login
     * @param userId   the usedId
     * @param stati    the stati of the user ({@link DisplayStatus})
     * @return the courses of the user with the given stati
     * @throws JDOMException if no document for the xml parser could be created
     * @throws IOException   if no InputStream could be created from the xmlString
     */
    public static List<IliasNode> getCoursesForUser(final ILIASSoapWebservicePortType endpoint,
                                                    final String sid, final int userId,
                                                    final IliasUtils.DisplayStatus... stati) throws IOException, JDOMException {
        List<IliasNode> courses = new ArrayList<>();

        String foundCourses = endpoint.getCoursesForUser(sid, XMLUtils.createCoursesResultXml(userId, stati));
        List<Integer> courseRefIds = XMLUtils.parseCourseRefIds(foundCourses);
        for (Integer courseRefId : courseRefIds) { // checked exceptions and lambdas...
            courses.add(XMLUtils.createsFromCourseNodeInfo(courseRefId, endpoint.getCourseXML(sid, courseRefId)));
        }

        return courses;
    }

    /**
     * Retrieves the refIds of all groups of the given refIds of the courses.
     * Courses can also contain folder trees. The maxDepth parameter limits the search depth.
     *
     * @param endpoint       the {@link ILIASSoapWebservicePortType}
     * @param sid            the sid of the user obtained at the login
     * @param userId         the usedId
     * @param courseRefIds   the refIds of the courses
     * @param maxFolderDepth the maximum folder depth to search
     * @return all refIds of groups found in the course
     */
    public static List<Integer> retrieveGroupRefIdsFromCourses(final ILIASSoapWebservicePortType endpoint,
                                                               final String sid, final int userId,
                                                               final List<Integer> courseRefIds, final int maxFolderDepth) throws IOException, JDOMException {
        List<Integer> groupRefIds = new ArrayList<>();
        for (Integer courseRefId : courseRefIds) {
            IliasUtils.retrieveGroupRefIdsFromNode(groupRefIds, endpoint, sid, userId, courseRefId, 0, maxFolderDepth);
        }
        return groupRefIds;
    }

    /**
     * Retrieves the refIds of all groups of the given refId of a node/object in the ilias tree.
     * A node can also contain folder trees. The maxDepth parameter limits the search depth.
     *
     * @param groupRefIds  the accumulated group ref ids
     * @param endpoint     the {@link ILIASSoapWebservicePortType}
     * @param sid          the sid of the user obtained at the login
     * @param userId       the usedId
     * @param nodeRefId    the refId of the node
     * @param currentDepth the current depth of the search
     * @param maxDepth     the maximum folder depth to search
     */
    private static void retrieveGroupRefIdsFromNode(final List<Integer> groupRefIds, final ILIASSoapWebservicePortType endpoint,
                                                    final String sid, final int userId,
                                                    final int nodeRefId,
                                                    final int currentDepth, final int maxDepth) throws IOException, JDOMException {
        if (currentDepth <= maxDepth) {
            List<Integer> folderChildrenRefIds = getRefIdsOfChildrenFromCurrentNode(endpoint, sid, userId, nodeRefId, IliasNode.Type.FOLDER);
            for (Integer folderChildRefId : folderChildrenRefIds) {
                // climb down the tree :)
                retrieveGroupRefIdsFromNode(groupRefIds, endpoint, sid, userId, folderChildRefId, currentDepth + 1, maxDepth);
            }

            // accumulate refIds from groups
            groupRefIds.addAll(getRefIdsOfChildrenFromCurrentNode(endpoint, sid, userId, nodeRefId, IliasNode.Type.GROUP));
        }
    }

    /**
     * Retrieves the children of the given {@link com.github.mavogel.ilias.model.IliasNode.Type} of
     * the given node with the nideRefId
     *
     * @param endpoint  the {@link ILIASSoapWebservicePortType}
     * @param sid       the sid of the user obtained at the login
     * @param userId    the usedId
     * @param nodeRefId the refId of the node
     * @param nodeType  the desired type of children nodes
     * @return the refIds of the children
     * @throws JDOMException if no document for the xml parser could be created
     * @throws IOException   if no InputStream could be created from the xmlString
     */
    private static List<Integer> getRefIdsOfChildrenFromCurrentNode(final ILIASSoapWebservicePortType endpoint,
                                                                    final String sid, final int userId,
                                                                    final int nodeRefId, final IliasNode.Type nodeType) throws IOException, JDOMException {
        String currentNodeXml = endpoint.getTreeChilds(sid, nodeRefId,
                IliasNode.Type.compose(nodeType), userId);
        return XMLUtils.parseRefIdsOfNodeType(nodeType, currentNodeXml);
    }

    /**
     * Retrieves all fileRefIds from the given groups.
     *
     * @param endpoint    the {@link ILIASSoapWebservicePortType}
     * @param sid         the sid of the user obtained at the login
     * @param userId      the usedId
     * @param groupRefIds the refIds of the groups
     * @return the refIds of the files in the groups
     */
    public static List<Integer> retrieveFileRefIdsFromGroups(final ILIASSoapWebservicePortType endpoint,
                                                             final String sid, final int userId,
                                                             final List<Integer> groupRefIds) throws IOException, JDOMException {
        List<Integer> fileRefIds = new ArrayList<>();
        for (Integer groupRefId : groupRefIds) {
            fileRefIds.addAll(getRefIdsOfChildrenFromCurrentNode(endpoint, sid, userId, groupRefId, IliasNode.Type.FILE));
        }
        return fileRefIds;
    }

    /**
     * Deletes a node in the ilias tree.
     *
     * @param endpoint   the {@link ILIASSoapWebservicePortType}
     * @param sid        the sid of the user obtained at the login
     * @param nodeRefIds the refIds of the node to delete
     * @throws RemoteException
     */
    public static void deleteObjects(final ILIASSoapWebservicePortType endpoint,
                                     final String sid, final List<Integer> nodeRefIds) throws RemoteException {
        for (Integer nodeRefId : nodeRefIds) {
//            TODO
//            boolean objectDeleted = endpoint.deleteObject(sid, nodeRefId);
//            System.out.printf("objectDeleted: %s%n", objectDeleted);
        }
    }

    /**
     * Removes all members from the given groups.
     *
     * @param endpoint    the {@link ILIASSoapWebservicePortType}
     * @param sid         the sid of the user obtained at the login
     * @param groupRefIds the refIds of the groups
     * @throws IOException
     * @throws JDOMException
     */
    public static void removeAllMembersFromGroups(final ILIASSoapWebservicePortType endpoint,
                                                  final String sid, final List<Integer> groupRefIds) throws IOException, JDOMException {
        for (Integer groupRefId : groupRefIds) {
            String groupXml = endpoint.getGroup(sid, groupRefId);
            List<Integer> groupMemberIds = XMLUtils.parseGroupMemberIds(groupXml);
            for (Integer groupMemberId : groupMemberIds) {
                removeMemberFromGroup(endpoint, sid, groupRefId, groupMemberId);
            }
        }
    }

    /**
     * Removes the member with the given id from the group.
     *
     * @param endpoint      the {@link ILIASSoapWebservicePortType}
     * @param sid           the sid of the user obtained at the login
     * @param groupRefId    the refId of the group
     * @param groupMemberId the id of the member to remove
     * @return <code>true</code> if the member was removed, <code>false</code> otherwise
     * @throws RemoteException
     */
    private static boolean removeMemberFromGroup(final ILIASSoapWebservicePortType endpoint, final String sid,
                                                 final Integer groupRefId, final Integer groupMemberId) throws RemoteException {
        boolean groupMemberExcluded = true;
//        TODO
//        groupMemberExcluded = endpoint.excludeGroupMember(sid, groupRefId, groupMemberId);
        System.out.println("excluded Member with id:" + groupMemberId + " -> " + groupMemberExcluded);
        return groupMemberExcluded;
    }

    /**
     * Sets the new registration start and end dates on all groups.
     *
     * @param endpoint          the {@link ILIASSoapWebservicePortType}
     * @param sid               the sid of the user obtained at the login
     * @param groupRefIds       the refIds of the groups to set the new dates
     * @param registrationStart the start of the registration
     * @param registrationEnd   the end of the registration
     * @throws IOException
     * @throws JDOMException
     */
    public static void setRegistrationDatesOnGroupes(final ILIASSoapWebservicePortType endpoint, final String sid,
                                                     final List<Integer> groupRefIds,
                                                     final LocalDateTime registrationStart, final LocalDateTime registrationEnd) throws IOException, JDOMException {

        final long newStart = toEpochSecond(registrationStart);
        final long newEnd = toEpochSecond(registrationEnd);
        System.out.println("new: " + newStart + " -> " + Instant.ofEpochSecond(newStart));
        for (Integer groupRefId : groupRefIds) {
//            TODO
//            String groupXml = endpoint.getGroup(sid, groupRefId);
//            String updatedGroupXml = XMLUtils.setRegistrationDates(groupXml, newStart, newEnd);
//            boolean isGroupUpdated = endpoint.updateGroup(sid, groupRefId, updatedGroupXml);
//            System.out.println("Group updated?: " + isGroupUpdated);
        }
    }

    /**
     * Converts a {@link LocalDate} into epoch seconds represented in the
     * time zone of the machine this tool is running. It's expected the ilias
     * server is running in the same time zone.
     *
     * @param localDateTime the local date time to convert
     * @return the seconds passed from the epoch
     */
    private static long toEpochSecond(final LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).toEpochSecond();
    }
}
