package com.github.mavogel.ilias.model;

/**
 * Created by mavogel on 9/5/16.
 */
public class UserDataIds {

    private int userId;
    private String sid;

    /**
     * C'tor
     *
     * @param userId the id of the
     * @param sid    the sid of the user obtained after login
     */
    public UserDataIds(final int userId, final String sid) {
        this.userId = userId;
        this.sid = sid;
    }

    /**
     * @return the user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return the sid
     */
    public String getSid() {
        return sid;
    }
}
