package com.github.mavogel.ilias;

import com.github.mavogel.client.ILIASSoapWebserviceLocator;
import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.utils.ConfigurationsUtils;
import com.github.mavogel.ilias.utils.LoginConfiguration;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Objects;

/**
 * Created by mavogel on 8/29/16.
 */
public class Starter {

    public static void main(String[] args) {
        Validate.notNull(args);
        Validate.noNullElements(args);
        Validate.isTrue(args.length == 1);
        Validate.isTrue(!args[0].isEmpty());
        run(ConfigurationsUtils.createLoginConfiguration(args[0]));
    }

    private static void run(LoginConfiguration loginConfiguration) {
        ILIASSoapWebservicePortType endpoint = null;
        String sid = "";
        try {
            ILIASSoapWebserviceLocator locator = new ILIASSoapWebserviceLocator();
            endpoint = locator.getILIASSoapWebservicePort();
            sid = endpoint.loginLDAP(loginConfiguration.getClient(), loginConfiguration.getUsername(), loginConfiguration.getPassword());
            System.out.printf("sid: %s%n", sid);
            int userIdBySid = endpoint.getUserIdBySid(sid);
            System.out.printf("user by sid: %d%n", userIdBySid);
            String coursesForUser = endpoint.getCoursesForUser(sid, "<result><colspecs><colspec name=\"user_id\"/><colspec name=\"status\"/></colspecs><row><column>" + userIdBySid + "</column><column>4</column></row></result>");
            System.out.printf("courses for user: %s%n", coursesForUser);
        } catch (javax.xml.rpc.ServiceException ex) {
            System.out.println(ex.getMessage());
        } catch (RemoteException ex) {
            // auth failed comes here
            System.out.println(ex.getMessage());
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
