package com.github.mavogel.ilias.utils;

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
}
