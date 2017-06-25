package com.github.mavogel.ilias.wrapper;/*
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
import com.github.mavogel.ilias.model.GroupUserModelFull;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.utils.XMLUtils;
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
public class SoapEndpoint implements IliasEndpoint {

    private static SoapEndpoint INSTANCE;
    private static Logger LOG = Logger.getLogger(SoapEndpoint.class);

    private ILIASSoapWebservicePortType endpoint;
    private static LoginConfiguration loginConfiguration;
    private UserDataIds userDataIds;

    /**
     * Creates the endpoint of the Ilias SOAP interface.
     *
     * @param loginConfiguration the config of the login
     * @throws Exception if the endpoint could not be established
     */
    public static SoapEndpoint createAndGetInstance(final LoginConfiguration loginConfiguration) throws Exception {
        if (INSTANCE != null) {
            throw new Exception("Instance already created. Use 'getInstance' instead");
        }

        SoapEndpoint.loginConfiguration = loginConfiguration;
        return SoapEndpoint.getInstance();
    }

    /**
     * The instance of the SOAP endpoint.
     *
     * @return the endpoint
     * @throws Exception if the endpoint could not be established
     */
    public static SoapEndpoint getInstance() throws Exception {
        if (INSTANCE == null) {
            INSTANCE = new SoapEndpoint();
        }

        return INSTANCE;
    }

    /**
     * C'tor
     */
    private SoapEndpoint() throws Exception {
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
    public UserDataIds getUserData(final LoginConfiguration loginConfiguration) throws Exception {
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
            return userDataIds;
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
    public void logout() throws Exception {
        if (userDataIds != null && endpoint != null) {
            try {
                boolean isLoggedOut = endpoint.logout(userDataIds.getSid());
                if (isLoggedOut) {
                    LOG.info("Successfully logged out for sid: '" + userDataIds.getSid() + "'");
                } else {
                    LOG.error("Could not log out!");
                }
            } catch (RemoteException e) {
                LOG.error("Could not log out: " + e.getMessage());
            }
        }
    }

    @Override
    public List<IliasNode> getCoursesForUser(final DisplayStatus... status) throws Exception {
        List<IliasNode> courses = new ArrayList<>();

        String foundCourses = endpoint.getCoursesForUser(userDataIds.getSid(),
                XMLUtils.createCoursesResultXml2(userDataIds.getUserId(), status)); // TODO rename later
        if (LOG.isDebugEnabled()) LOG.debug("CoursesXML for user : " + foundCourses);
        List<Integer> courseRefIds = XMLUtils.parseCourseRefIds(foundCourses);
        for (Integer courseRefId : courseRefIds) { // checked exceptions and lambdas...
            IliasNode courseNode = XMLUtils.createsFromCourseNodeInfo(courseRefId, endpoint.getCourseXML(userDataIds.getSid(), courseRefId));
            if (LOG.isDebugEnabled()) LOG.debug("Found Course: " + courseNode);
            courses.add(courseNode);
        }
        return courses;
    }

    @Override
    public List<IliasNode> getGroupRefIdsFromCourses(final IliasNode course, final int maxFolderDepth) throws Exception {
        List<IliasNode> groups = new ArrayList<>();
        LOG.info("-- Collecting groups from '" + course.getTitle() + "'");
        this.retrieveGroupRefIdsFromNode(groups, course.getRefId(), 0, maxFolderDepth);
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
        return XMLUtils.parseRefIdsOfNodeType(nodeType, currentNodeXml);
    }

    @Override
    public void grantFileUploadPermissionForMembers(final List<IliasNode> groups) throws Exception {

    }

    @Override
    public List<GroupUserModelFull> getUsersForGroups(final List<IliasNode> groups) throws Exception {
        return null;
    }

    @Override
    public List<IliasNode> getFilesFromGroups(final List<IliasNode> groups) throws Exception {
        return null;
    }

    @Override
    public void deleteObjectNodes(final List<IliasNode> files) throws Exception {

    }

    @Override
    public void removeAllMembersFromGroups(final List<IliasNode> groups) throws Exception {

    }

    @Override
    public void setMaxMembersOnGroups(final List<IliasNode> groups) throws Exception {

    }

    @Override
    public void setRegistrationDatesOnGroups(final List<IliasNode> groups, final LocalDateTime start, final LocalDateTime end) throws Exception {

    }

}
