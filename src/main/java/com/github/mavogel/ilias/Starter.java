package com.github.mavogel.ilias;

import com.github.mavogel.client.ILIASSoapWebserviceLocator;
import com.github.mavogel.client.ILIASSoapWebservicePortType;

import java.rmi.RemoteException;

/**
 * Created by mavogel on 8/29/16.
 */
public class Starter {

    public static void main(String[] args) {
        ILIASSoapWebservicePortType endpoint = null;
        String sid = "";
        try {
            ILIASSoapWebserviceLocator locator = new ILIASSoapWebserviceLocator();
            endpoint = locator.getILIASSoapWebservicePort();
            String client = "";
            String username = "";
            String password = "";
            sid = endpoint.loginLDAP(client, username, password);
            System.out.printf("sid: %s%n", sid);
            int userIdBySid = endpoint.getUserIdBySid(sid);
            System.out.printf("user by sid: %d%n", userIdBySid);
            String coursesForUser = endpoint.getCoursesForUser(sid, "<result><colspecs><colspec name=\"user_id\"/><colspec name=\"status\"/></colspecs><row><column>" + userIdBySid + "</column><column>4</column></row></result>");
            System.out.printf("courses for user: %s%n", coursesForUser);
        } catch (javax.xml.rpc.ServiceException ex) {
            System.out.println(ex.getMessage());
        } catch (java.rmi.RemoteException ex) {
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
