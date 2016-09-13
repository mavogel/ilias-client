package com.github.mavogel.ilias.utils;

import com.github.mavogel.client.ILIASSoapWebserviceLocator;
import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.GroupUserModel;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mavogel on 9/5/16.
 */
public class IliasUtils {

    private static Logger LOG = Logger.getLogger(IliasUtils.class);

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
     * @throws javax.xml.rpc.ServiceException if the endpoint could not be established
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
     * @throws RemoteException if the login to retrieve the user data could not be performed successfully
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
                throw new UnsupportedOperationException("Login with CAS is not yet supported");
        }
        return new UserDataIds(endpoint.getUserIdBySid(sid), sid);
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
     * @return all groups found in the course
     */
    public static List<IliasNode> retrieveGroupRefIdsFromCourses(final ILIASSoapWebservicePortType endpoint,
                                                                 final String sid, final int userId,
                                                                 final List<Integer> courseRefIds, final int maxFolderDepth) throws IOException, JDOMException {
        List<IliasNode> groupNodes = new ArrayList<>();
        for (Integer courseRefId : courseRefIds) {
            IliasUtils.retrieveGroupRefIdsFromNode(groupNodes, endpoint, sid, userId, courseRefId, 0, maxFolderDepth);
        }
        return groupNodes;
    }

    /**
     * Retrieves the refIds of all groups of the given refId of a node/object in the ilias tree.
     * A node can also contain folder trees. The maxDepth parameter limits the search depth.
     *
     * @param groupNodes   the accumulated group ref ids
     * @param endpoint     the {@link ILIASSoapWebservicePortType}
     * @param sid          the sid of the user obtained at the login
     * @param userId       the usedId
     * @param nodeRefId    the refId of the node
     * @param currentDepth the current depth of the search
     * @param maxDepth     the maximum folder depth to search
     */
    private static void retrieveGroupRefIdsFromNode(final List<IliasNode> groupNodes, final ILIASSoapWebservicePortType endpoint,
                                                    final String sid, final int userId,
                                                    final int nodeRefId,
                                                    final int currentDepth, final int maxDepth) throws IOException, JDOMException {
        LOG.debug("Depth " + currentDepth + " of node: " + nodeRefId);
        if (currentDepth <= maxDepth) {
            List<IliasNode> folderChildrenNodes = getRefIdsOfChildrenFromCurrentNode(endpoint, sid, userId, nodeRefId, IliasNode.Type.FOLDER);
            for (IliasNode folderChildNode : folderChildrenNodes) {
                // climb down the tree :)
                retrieveGroupRefIdsFromNode(groupNodes, endpoint, sid, userId, folderChildNode.getRefId(), currentDepth + 1, maxDepth);
            }

            // accumulate refIds from groups
            groupNodes.addAll(getRefIdsOfChildrenFromCurrentNode(endpoint, sid, userId, nodeRefId, IliasNode.Type.GROUP));
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
     * @return the information of the children as {@link IliasNode}
     * @throws JDOMException if no document for the xml parser could be created
     * @throws IOException   if no InputStream could be created from the xmlString
     */
    private static List<IliasNode> getRefIdsOfChildrenFromCurrentNode(final ILIASSoapWebservicePortType endpoint,
                                                                      final String sid, final int userId,
                                                                      final int nodeRefId, final IliasNode.Type nodeType) throws IOException, JDOMException {
        String currentNodeXml = endpoint.getTreeChilds(sid, nodeRefId,
                IliasNode.Type.compose(nodeType), userId);
        return XMLUtils.parseRefIdsOfNodeType(nodeType, currentNodeXml);
    }

    /**
     * Retrieves all fileRefIds from the given groups.
     *
     * @param endpoint   the {@link ILIASSoapWebservicePortType}
     * @param sid        the sid of the user obtained at the login
     * @param userId     the usedId
     * @param groupNodes the group nodes
     * @return the refIds of the files in the groups
     */
    public static List<IliasNode> retrieveFileRefIdsFromGroups(final ILIASSoapWebservicePortType endpoint,
                                                               final String sid, final int userId,
                                                               final List<IliasNode> groupNodes) throws IOException, JDOMException {
        List<IliasNode> fileRefIds = new ArrayList<>();
        for (IliasNode groupNode : groupNodes) {
            fileRefIds.addAll(getRefIdsOfChildrenFromCurrentNode(endpoint, sid, userId, groupNode.getRefId(), IliasNode.Type.FILE));
        }
        return fileRefIds;
    }

    /**
     * Deletes a node in the ilias tree.
     *
     * @param endpoint the {@link ILIASSoapWebservicePortType}
     * @param sid      the sid of the user obtained at the login
     * @param nodes    the nodes to delete
     * @throws RemoteException
     */
    public static void deleteObjects(final ILIASSoapWebservicePortType endpoint,
                                     final String sid, final List<IliasNode> nodes) throws RemoteException {
        int i = 0;
        for (IliasNode node : nodes) {
//            TODO activate
//            boolean objectDeleted = endpoint.deleteObject(sid, node.getRefId());
//            if (objectDeleted) {
//                // TODO optional clear line after each
//                LOG.debug("Processing node [" + i + "] of " + nodes.size());
//            } else {
//                LOG.error("Could not delete ilias node: " + node);
//            }
            i++;
        }
    }

    /**
     * Removes all members from the given groups. Prints the users which could not be removed.
     *
     * @param endpoint   the {@link ILIASSoapWebservicePortType}
     * @param sid        the sid of the user obtained at the login
     * @param groupNodes the group nodes
     * @throws JDOMException if no document for the xml parser could be created
     * @throws IOException   if no InputStream could be created from the xmlString
     */
    public static void removeAllMembersFromGroups(final ILIASSoapWebservicePortType endpoint,
                                                  final String sid, final List<IliasNode> groupNodes) throws IOException, JDOMException {
        List<GroupUserModel> unremovedUsers = new ArrayList<>();
        int i = 0;
        for (IliasNode groupNode : groupNodes) {
            String groupXml = endpoint.getGroup(sid, groupNode.getRefId());
            List<Integer> groupMemberIds = XMLUtils.parseGroupMemberIds(groupXml);
            unremovedUsers.add(removeMembersFromGroup(endpoint, sid, groupNode, groupMemberIds));
            // TODO optional clear line after each
            LOG.debug("Processing group [" + i + "] of " + groupNodes.size());
            i++;
        }

        if (!unremovedUsers.isEmpty() && unremovedUsers.stream().anyMatch(u -> u.hasMembers())) {
            LOG.error("Could not remove users from group(s): ");
            unremovedUsers.stream().filter(u -> u.hasMembers()).forEach(System.out::println);
        }
    }

    /**
     * Removes the members with the given ids from the group.
     *
     * @param endpoint       the {@link ILIASSoapWebservicePortType}
     * @param sid            the sid of the user obtained at the login
     * @param groupNode      the group node
     * @param groupMemberIds the ids of the members of the group
     * @return a {@link GroupUserModel} containing the member which could not be removed from the group.
     * {@link GroupUserModel#hasMembers()} return <code>false</code> if all user could be removed.
     */
    private static GroupUserModel removeMembersFromGroup(final ILIASSoapWebservicePortType endpoint, final String sid,
                                                         final IliasNode groupNode, final List<Integer> groupMemberIds) {
        GroupUserModel unremovedUsers = new GroupUserModel(groupNode);
        for (Integer groupMemberId : groupMemberIds) {
            try {
                // TODO activate
                boolean groupMemberExcluded = false; //endpoint.excludeGroupMember(sid, groupNode.getRefId(), groupMemberId);
                // TODO logging
                LOG.debug("excluded Member with id:" + groupMemberId + " -> " + groupMemberExcluded);
                if (!groupMemberExcluded) {
                    unremovedUsers.addGroupMemberId(groupMemberId);
                }
            } catch (Exception e) {
                unremovedUsers.addGroupMemberId(groupMemberId);
            }
        }

        return unremovedUsers;
    }

    /**
     * Sets the new registration start and end dates on all groups.
     *
     * @param endpoint          the {@link ILIASSoapWebservicePortType}
     * @param sid               the sid of the user obtained at the login
     * @param groupNodes        the groups to set the new dates
     * @param registrationStart the start of the registration
     * @param registrationEnd   the end of the registration
     * @throws JDOMException if no document for the xml parser could be created
     * @throws IOException   if no InputStream could be created from the xmlString
     */
    public static void setRegistrationDatesOnGroups(final ILIASSoapWebservicePortType endpoint, final String sid,
                                                    final List<IliasNode> groupNodes,
                                                    final LocalDateTime registrationStart, final LocalDateTime registrationEnd) throws IOException, JDOMException {

        final long newStart = toEpochSecond(registrationStart);
        final long newEnd = toEpochSecond(registrationEnd);
        int i = 0;
        for (IliasNode groupNode : groupNodes) {
//            TODO activate
//            String groupXml = endpoint.getGroup(sid, groupNode.getRefId());
//            String updatedGroupXml = XMLUtils.setRegistrationDates(groupXml, newStart, newEnd);
//            boolean isGroupUpdated = endpoint.updateGroup(sid, groupNode.getRefId(), updatedGroupXml);
//            if (isGroupUpdated) {
//                LOG.debug("Updated group [" + i + "] - " + groupNode.getTitle() + " of " + groupNodes.size());
//            } else {
//                LOG.error("Failed to set registration date on group [" + i + "] - " + groupNode.getTitle());
//            }
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
