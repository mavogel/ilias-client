package com.github.mavogel.ilias.utils;

import com.github.mavogel.client.ILIASSoapWebserviceLocator;
import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by mavogel on 9/5/16.
 */
public class IliasUtils {

    /**
     * The DisplayStatus has four values:
     * <ul>
     *     <li>MEMBER = 1</li>
     *     <li>TUTOR = 2</li>
     *     <li>ADMIN = 4</li>
     *     <li>OWNER = 8</li>
     * </ul>
     * and determines which courses should be returned.
     *
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
     * The types of (tree) objects in ilias.<br>
     * It can be used in {@link com.github.mavogel.client.ILIASSoapWebservicePortType#getTreeChilds(String, int, String[], int)}
     */
    public enum ObjectTypes {

        GROUP("grp"),
        FOLDER("fold"),
        FILE("file");

        private String xmlShortName;

        ObjectTypes(final String xmlShortName) {
            this.xmlShortName = xmlShortName;
        }

        /**
         * @return the xml short name on the result sets
         */
        public String getXmlShortName() {
            return xmlShortName;
        }

        /**
         * Composes the String array needed from the {@link com.github.mavogel.client.ILIASSoapWebservicePortType}
         *
         * @param types the desired types
         * @return the String array with the needed xml names of the {@link ObjectTypes}
         */
        public static String[] compose(ObjectTypes... types) {
            if(types == null) {
                return new String[]{};
            }
            return Arrays.stream(types).map(type -> type.getXmlShortName()).collect(Collectors.toList()).toArray(new String[]{});
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
}
