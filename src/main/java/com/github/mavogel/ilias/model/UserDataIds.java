package com.github.mavogel.ilias.model;

import com.github.mavogel.client.IlUserData;

/**
 * Created by mavogel on 9/5/16.
 */
public class ExtendedIUserData {

    private IlUserData basicUserData;
    private String sid;

    /**
     * C'tor
     *
     * @param basicUserData the user data from ilias
     * @param sid the sid of the user
     */
    public ExtendedIUserData(final IlUserData basicUserData, final String sid) {
        this.basicUserData = basicUserData;
        this.sid = sid;
    }

    /**
     * @return the user data
     */
    public IlUserData getBasicUserData() {
        return basicUserData;
    }

    /**
     * @return the sid
     */
    public String getSid() {
        return sid;
    }
}
