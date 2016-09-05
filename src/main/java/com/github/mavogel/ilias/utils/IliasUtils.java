package com.github.mavogel.ilias.utils;

import com.github.mavogel.client.ILIASSoapWebserviceLocator;
import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;
import org.jdom.JDOMException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

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
     * @throws IOException
     * @throws JDOMException
     */
    private static List<Integer> getRefIdsOfChildrenFromCurrentNode(final ILIASSoapWebservicePortType endpoint,
                                                                    final String sid, final int userId,
                                                                    final int nodeRefId, final IliasNode.Type nodeType) throws IOException, JDOMException {
        String currentNodeXml = endpoint.getTreeChilds(sid, nodeRefId,
                IliasNode.Type.compose(nodeType), userId);
        return XMLUtils.parseRefIdsOfNodeType(nodeType, currentNodeXml);
    }
}
