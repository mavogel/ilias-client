package com.github.mavogel.ilias.model;

import com.github.mavogel.ilias.utils.IliasUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by mavogel on 9/5/16.
 */
public class IliasNode {

    /**
     * The types of (tree) nodes in ilias.<br>
     * It can be used in {@link com.github.mavogel.client.ILIASSoapWebservicePortType#getTreeChilds(String, int, String[], int)}
     */
    public enum Type {

        ROOT("root"),
        CATEGORY("cat"),
        COURSE("crs"),
        GROUP("grp"),
        FOLDER("fold"),
        FILE("file");

        private String xmlShortName;

        Type(final String xmlShortName) {
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
         * @return the String array with the needed xml names of the {@link Type}
         */
        public static String[] compose(Type... types) {
            if (types == null) {
                return new String[]{};
            }
            return Arrays.stream(types).map(type -> type.getXmlShortName()).collect(Collectors.toList()).toArray(new String[]{});
        }
    }

    /**
     * Members
     */
    private int refId;
    private Type nodeType;
    private String title;

    /**
     * An node of the tree in ilias.
     *
     * @param refId its refId
     * @param nodeType the {@link Type}
     * @param title
     */
    public IliasNode(final int refId, final Type nodeType, final String title) {
        this.refId = refId;
        this.nodeType = nodeType;
        this.title = title;
    }

    /**
     * @return the ref id
     */
    public int getRefId() {
        return refId;
    }

    /**
     * @return the nodeType
     */
    public Type getNodeType() {
        return nodeType;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
}
