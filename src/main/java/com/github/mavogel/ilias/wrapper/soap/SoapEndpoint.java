package com.github.mavogel.ilias.wrapper.soap;/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2017 Manuel Vogel
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 *  https://opensource.org/licenses/MIT
 */

import com.github.mavogel.client.ILIASSoapWebserviceLocator;
import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.*;
import com.github.mavogel.ilias.utils.Defaults;
import com.github.mavogel.ilias.wrapper.AbstractIliasEndpoint;
import com.github.mavogel.ilias.wrapper.DisplayStatus;
import com.github.mavogel.ilias.wrapper.IliasEndpoint;
import com.github.mavogel.ilias.wrapper.PermissionOperation;
import org.apache.log4j.Logger;

import javax.xml.rpc.ServiceException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mavogel on 6/25/17.
 * <p>
 * The SOAP endpoint wrapper for the Ilias backend.
 */
public class SoapEndpoint extends AbstractIliasEndpoint {

    private static Logger LOG = Logger.getLogger(SoapEndpoint.class);
    private ILIASSoapWebservicePortType endpoint;

    /**
     * C'tor (@see {@link AbstractIliasEndpoint})
     *
     * @param loginConfiguration the login configuration data
     * @throws Exception @see {@link AbstractIliasEndpoint}
     */
    public SoapEndpoint(final LoginConfiguration loginConfiguration) throws Exception {
        super(loginConfiguration);
    }

    @Override
    protected void createAndSetEndpoint() throws Exception {
        ILIASSoapWebserviceLocator locator = new ILIASSoapWebserviceLocator();
        locator.setILIASSoapWebservicePortEndpointAddress(loginConfiguration.getEndpoint());
        if (LOG.isDebugEnabled()) LOG.debug("Creating endpoint at " + loginConfiguration.getEndpoint());
        try {
            endpoint = locator.getILIASSoapWebservicePort();
        } catch (ServiceException e) {
            LOG.error("Could not establish webservice at '" + loginConfiguration.getEndpoint() + "'");
            throw new Exception();
        }
    }

    @Override
    protected void getAndSetUserData() throws Exception {
        try {
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
            userDataIds = new UserDataIds(endpoint.getUserIdBySid(sid), sid, loginConfiguration.getUsername());
        } catch (RemoteException e) {
            Throwable cause = e.getCause();
            if (cause instanceof UnknownHostException) {
                LOG.error("Could not establish connection to endpoint '" + loginConfiguration.getEndpoint() + "'");
            } else {
                LOG.error("Error retrieving the user data: " + e.getMessage());
            }
            throw new Exception();
        }
    }

    @Override
    public boolean logout() {
        if (userDataIds != null && endpoint != null) {
            try {
                boolean isLoggedOut = endpoint.logout(userDataIds.getSid());
                if (isLoggedOut) {
                    LOG.info("Successfully logged out for sid: '" + userDataIds.getSid() + "'");
                } else {
                    LOG.error("Could not log out!");
                }
                return isLoggedOut;
            } catch (RemoteException e) {
                LOG.error("Could not log out: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    public List<IliasNode> getCoursesForUser(final DisplayStatus... status) throws Exception {
        List<IliasNode> courses = new ArrayList<>();

        String foundCourses = endpoint.getCoursesForUser(userDataIds.getSid(),
                SoapXMLUtils.createCoursesResultXml2(userDataIds.getUserId(), status)); // TODO rename later
        if (LOG.isDebugEnabled()) LOG.debug("CoursesXML for user : " + foundCourses);
        List<Integer> courseRefIds = SoapXMLUtils.parseCourseRefIds(foundCourses);
        for (Integer courseRefId : courseRefIds) { // checked exceptions and lambdas...
            IliasNode courseNode = SoapXMLUtils.createsFromCourseNodeInfo(courseRefId, endpoint.getCourseXML(userDataIds.getSid(), courseRefId));
            if (LOG.isDebugEnabled()) LOG.debug("Found Course: " + courseNode);
            courses.add(courseNode);
        }
        return courses;
    }

    @Override
    public List<IliasNode> getGroupRefIdsFromCourses(final IliasNode course) throws Exception {
        List<IliasNode> groups = new ArrayList<>();
        LOG.info("-- Collecting groups from '" + course.getTitle() + "'");
        this.retrieveGroupRefIdsFromNode(groups, course.getRefId(), 0, loginConfiguration.getMaxFolderDepth());
        return groups;
    }

    /**
     * Retrieves the refIds of all groups of the given refId of a node/object in the ilias tree.
     * A node can also contain folder trees. The maxDepth parameter limits the search depth.
     *
     * @param groups       the accumulated group ref ids
     * @param nodeRefId    the refId of the node
     * @param currentDepth the current depth of the search
     * @param maxDepth     the maximum folder depth to search
     */
    private void retrieveGroupRefIdsFromNode(final List<IliasNode> groups, final int nodeRefId,
                                             final int currentDepth, final int maxDepth) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Retrieve Group refIds in depth " + currentDepth + " of node: " + nodeRefId);
        }
        if (currentDepth <= maxDepth) {
            List<IliasNode> folderChildrenNodes = getRefIdsOfChildrenFromCurrentNode(nodeRefId, IliasNode.Type.FOLDER);
            for (IliasNode folderChildNode : folderChildrenNodes) {
                // climb down the tree :)
                retrieveGroupRefIdsFromNode(groups, folderChildNode.getRefId(), currentDepth + 1, maxDepth);
            }

            // accumulate refIds from groups
            groups.addAll(getRefIdsOfChildrenFromCurrentNode(nodeRefId, IliasNode.Type.GROUP));
        }
    }

    /**
     * Retrieves the children of the given {@link com.github.mavogel.ilias.model.IliasNode.Type} of
     * the given node with the nideRefId
     *
     * @param nodeRefId the refId of the node
     * @param nodeType  the desired type of children nodes
     * @return the information of the children as {@link IliasNode}
     */
    private List<IliasNode> getRefIdsOfChildrenFromCurrentNode(final int nodeRefId, final IliasNode.Type nodeType) throws Exception {
        String currentNodeXml = endpoint.getTreeChilds(userDataIds.getSid(), nodeRefId,
                IliasNode.Type.compose(nodeType), userDataIds.getUserId());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Getting RefIds of children of node with refId + " + nodeRefId + " and of type " + nodeType.name() + " with xml:\n " + currentNodeXml);
        }
        return SoapXMLUtils.parseRefIdsOfNodeType(nodeType, currentNodeXml);
    }

    @Override
    public void grantFileUploadPermissionForMembers(final List<IliasNode> groups) throws Exception {
        for (IliasNode group : groups) {
            String localRolesForGroupXML = null;
            try {
                localRolesForGroupXML = endpoint.getLocalRoles(userDataIds.getSid(), group.getRefId());
            } catch (RemoteException e) {
                LOG.error("Local roles could not be retrieved for group '" + group.getRefId() + " - " + group.getTitle() + " because of: " + e.getMessage());
                LOG.error("Continuing with next one");
                continue;
            }

            int roleId = SoapXMLUtils.parseGroupMemberRoleId(localRolesForGroupXML);

            try {
                boolean isPermissionGranted = endpoint.grantPermissions(userDataIds.getSid(), group.getRefId(), roleId,
                        PermissionOperation.build(PermissionOperation.VISIBLE,
                                PermissionOperation.READ,
                                PermissionOperation.JOIN,
                                PermissionOperation.LEAVE,
                                PermissionOperation.CREATE_FILE));
                if (isPermissionGranted) {
                    LOG.info("File Upload permission granted on group '" + group.getRefId() + " - " + group.getTitle());
                } else {
                    LOG.error("File Upload permission NOT granted on group '" + group.getRefId() + " - " + group.getTitle());
                }
            } catch (RemoteException e) {
                LOG.error("File Upload permission NOT granted on group '" + group.getRefId() + " - " + group.getTitle() + " because of: " + e.getMessage());
            }
        }
    }

    @Override
    public List<GroupUserModelFull> getUsersForGroups(final List<IliasNode> groups) throws Exception {
        List<GroupUserModelFull> groupUserModels = new ArrayList<>();
        LOG.info("-- Collecting users for groups. Be patient...");
        for (IliasNode group : groups) {
            LOG.info("--- Processing '" + group.getTitle() + "'");
            String localRolesForGroupXML = endpoint.getLocalRoles(userDataIds.getSid(), group.getRefId());

            int roleId = SoapXMLUtils.parseGroupMemberRoleId(localRolesForGroupXML);
            String usersForRoleXML = endpoint.getUsersForRole(userDataIds.getSid(), roleId,
                    Defaults.ATTACH_ROLES, Defaults.IS_ACTIVE);

            List<IliasUser> userRecords = SoapXMLUtils.parseIliasUserRecordsFromRole(usersForRoleXML);
            groupUserModels.add(new GroupUserModelFull(group, userRecords));
        }

        return groupUserModels;
    }

    @Override
    public List<IliasNode> getFilesFromGroups(final List<IliasNode> groups) throws Exception {
        List<IliasNode> fileRefIds = new ArrayList<>();
        for (IliasNode group : groups) {
            List<IliasNode> fileNodes = getRefIdsOfChildrenFromCurrentNode(group.getRefId(), IliasNode.Type.FILE);
            if (fileNodes.isEmpty()) {
                LOG.info("No files to remove for group '" + group.getTitle() + "'");
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding file idRefs from group '" + group.getRefId() + " - " + group.getTitle() + "': " + fileNodes);
            }
            fileRefIds.addAll(fileNodes);
        }
        return fileRefIds;
    }

    @Override
    public void deleteObjectNodes(final List<IliasNode> nodes) throws Exception {
        for (IliasNode node : nodes) {
            try {
                boolean objectDeleted = endpoint.deleteObject(userDataIds.getSid(), node.getRefId());
                if (objectDeleted) {
                    LOG.info("Delete node '" + node.getRefId() + " - " + node.getTitle() + "'");
                } else {
                    LOG.error("Could not delete node '" + node.getRefId() + " - " + node.getTitle() + "'");
                }
            } catch (RemoteException e) {
                LOG.error("Could not delete node '" + node.getRefId() + " - " + node.getTitle() + "' due to a connection problem: " + e.getMessage());
            }
        }
    }

    @Override
    public void removeAllMembersFromGroups(final List<IliasNode> groups) throws Exception {
        List<GroupUserModel> unremovedUsers = new ArrayList<>();
        for (IliasNode group : groups) {
            if (LOG.isDebugEnabled()) LOG.debug("Removing member from group: " + group.getTitle());

            String groupXml = endpoint.getGroup(userDataIds.getSid(), group.getRefId());
            List<Integer> groupMemberIds = SoapXMLUtils.parseGroupMemberIds(groupXml);
            if (groupMemberIds.isEmpty()) LOG.info("No members to remove from Group '" + group.getTitle() + "'");

            unremovedUsers.add(removeMembersFromGroup(group, groupMemberIds));
        }

        if (!unremovedUsers.isEmpty() && unremovedUsers.stream().anyMatch(GroupUserModel::hasMembers)) {
            LOG.error("Could not remove users from group(s): ");
            unremovedUsers.stream().filter(GroupUserModel::hasMembers).forEach(LOG::error);
        }
    }

    /**
     * Removes the members with the given ids from the group.
     *
     * @param group          the group node
     * @param groupMemberIds the ids of the members of the group
     * @return a {@link GroupUserModel} containing the member which could not be removed from the group.
     * {@link GroupUserModel#hasMembers()} return <code>false</code> if all user could be removed.
     */
    private GroupUserModel removeMembersFromGroup(final IliasNode group, final List<Integer> groupMemberIds) {
        GroupUserModel unremovedUsers = new GroupUserModel(group);
        for (Integer groupMemberId : groupMemberIds) {
            try {
                boolean groupMemberExcluded = endpoint.excludeGroupMember(userDataIds.getSid(), group.getRefId(), groupMemberId);
                if (groupMemberExcluded) {
                    LOG.info("Excluded member with id:" + groupMemberId + " from group '" + group.getRefId() + " - " + group.getTitle() + "'");
                } else {
                    LOG.error("Could not exclude member with id:" + groupMemberId + " from group '" + group.getRefId() + " - " + group.getTitle() + "'");
                    unremovedUsers.addGroupMemberId(groupMemberId);
                }
            } catch (Exception e) {
                LOG.error("Could not exclude member with id:" + groupMemberId + " from group '" + group.getRefId() + " - " + group.getTitle() + "' because of " + e.getMessage());
                unremovedUsers.addGroupMemberId(groupMemberId);
            }
        }

        return unremovedUsers;
    }


    @Override
    public void setMaxMembersOnGroups(final List<IliasNode> groups, final int maxGroupMembers) throws Exception {
        for (IliasNode group : groups) {
            String groupXml = endpoint.getGroup(userDataIds.getSid(), group.getRefId());
            String updatedGroupXml = SoapXMLUtils.setMaxGroupMembers(groupXml, maxGroupMembers);
            boolean isGroupUpdated = endpoint.updateGroup(userDataIds.getSid(), group.getRefId(), updatedGroupXml);
            if (isGroupUpdated) {
                LOG.info("Updated group '" + group.getRefId() + " - " + group.getTitle() + "' with new max group members " + maxGroupMembers);
            } else {
                LOG.error("Failed to set max members on group '" + group.getRefId() + " - " + group.getTitle() + "'");
            }
        }
    }

    @Override
    public void setRegistrationDatesOnGroups(final List<IliasNode> groups, final LocalDateTime start, final LocalDateTime end) throws Exception {
        final long newStart = toEpochSecond(start);
        final long newEnd = toEpochSecond(end);
        for (IliasNode group : groups) {
            String groupXml = endpoint.getGroup(userDataIds.getSid(), group.getRefId());
            String updatedGroupXml = SoapXMLUtils.setRegistrationDates(groupXml, newStart, newEnd);
            boolean isGroupUpdated = endpoint.updateGroup(userDataIds.getSid(), group.getRefId(), updatedGroupXml);
            if (isGroupUpdated) {
                LOG.info("Updated group '" + group.getRefId() + " - " + group.getTitle() + "' with new registration period starting at " + start + " and ending at " + end);
            } else {
                LOG.error("Failed to set registration date on group '" + group.getRefId() + " - " + group.getTitle() + "'");
            }
        }
    }
}
