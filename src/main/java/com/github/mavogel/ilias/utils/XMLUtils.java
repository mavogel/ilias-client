package com.github.mavogel.ilias.utils;

import com.github.mavogel.ilias.model.IliasNode;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.Validate;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mavogel on 9/5/16.
 */
public class XMLUtils {

    /**
     * Creates the result xml string used for getting the courses of a user.
     *
     * @param userId       the id of the user
     * @param displayStati one or more stati of the courses which should be displayed
     * @return a result set in the following form. ref_id is the important column.
     * <pre>
     * &lt;result&gt;
     *     &lt;colspecs&gt;
     *     &lt;colspec idx=&quot;0&quot; name=&quot;ref_id&quot;/&gt;
     *     &lt;colspec idx=&quot;1&quot; name=&quot;xml&quot;/&gt;
     *     &lt;colspec idx=&quot;2&quot; name=&quot;parent_ref_id&quot;/&gt;
     *   &lt;/colspecs&gt;
     *   &lt;rows&gt;
     *     &lt;row&gt;
     *       &lt;column&gt;44525&lt;/column&gt;
     *       &lt;column&gt;&lt;!-- lots of encoded xml... --&gt;&lt;/column&gt;
     *       &lt;column&gt;7266&lt;/column&gt;
     *     &lt;/row&gt;
     *   &lt;/rows&gt;
     * &lt;/result&gt;
     * </pre>
     */
    public static String createCoursesResultXml(final int userId, final IliasUtils.DisplayStatus... displayStati) {
        Validate.notNull(displayStati, "The displaystati are null");
        Validate.noNullElements(displayStati, "One or more displayStati are empty");
        return new StringBuilder()
                .append("<result><colspecs><colspec name=\"user_id\"/><colspec name=\"status\"/></colspecs><row><column>")
                .append(userId).append("</column><column>")
                .append(Arrays.stream(displayStati).mapToInt(s -> s.asNumber()).reduce((s1, s2) -> s1 | s2).getAsInt())
                .append("</column></row></result>").toString();
    }

    /**
     * Parses the xml of the courses and retrieves the refIds.
     * It is assumed that the first column node in the rows->row nodes contains the refId
     * <p>
     * <pre>
     * &lt;result&gt;
     *     &lt;colspecs&gt;
     *     &lt;colspec idx=&quot;0&quot; name=&quot;ref_id&quot;/&gt;
     *     &lt;colspec idx=&quot;1&quot; name=&quot;xml&quot;/&gt;
     *     &lt;colspec idx=&quot;2&quot; name=&quot;parent_ref_id&quot;/&gt;
     *   &lt;/colspecs&gt;
     *   &lt;rows&gt;
     *     &lt;row&gt;
     *       &lt;column&gt;44525&lt;/column&gt;
     *       &lt;column&gt;&lt;!-- lots of encoded xml... --&gt;&lt;/column&gt;
     *       &lt;column&gt;7266&lt;/column&gt;
     *     &lt;/row&gt;
     *   &lt;/rows&gt;
     * &lt;/result&gt;
     * </pre>
     *
     * @param coursesXml the xml of the courses
     * @return the refIds of the courses
     * @throws JDOMException if no document for the xml parser could be created
     * @throws IOException   if no InputStream could be created from the xmlString
     */
    public static List<Integer> parseCourseRefIds(final String coursesXml) throws JDOMException, IOException {
        Document doc = createSaxDocFromString(coursesXml);
        List<Integer> courseRefIds = new ArrayList<>();
        final int indexOfRefIdColumn = 0; // TODO hardwired: make it more dynamically if the column switches

        Element rootElement = doc.getRootElement();
        Element rowsRoot = rootElement.getChild("rows");
        List<Element> rows = rowsRoot.getChildren("row");
        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            List<Element> column = row.getChildren("column");
            courseRefIds.add(Integer.valueOf(column.get(indexOfRefIdColumn).getTextTrim()));
        }
        return courseRefIds;
    }

    /**
     * Creates an {@link IliasNode} from the course info xml.
     *
     * @param courseRefId the course ref which is not contained in the course info xml
     * @param courseInfoXml the course info xml to parse
     * @return the {@link IliasNode}
     * @throws JDOMException if no document for the xml parser could be created
     * @throws IOException   if no InputStream could be created from the xmlString
     */
    public static IliasNode createsFromCourseNodeInfo(final int courseRefId, final String courseInfoXml) throws JDOMException, IOException {
        Document doc = createSaxDocFromString(courseInfoXml);

        Element rootElement = doc.getRootElement();
        String title = rootElement.getChild("MetaData").getChild("General").getChild("Title").getTextTrim();
        return new IliasNode(courseRefId, IliasNode.Type.COURSE, title);
    }

    /**
     * Build an XML {@link Document} from string
     *
     * @param xmlString the xml string.
     * @return the {@link Document}
     * @throws JDOMException if no document for the xml parser could be created
     * @throws IOException   if no InputStream could be created from the xmlString
     */
    private static Document createSaxDocFromString(final String xmlString) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(new ByteArrayInputStream(xmlString.getBytes()));
    }

    /**
     * Parses the refIds of all object of the given type in the xml representation of the node/object.
     *
     * @param nodeType the type of the node
     * @param nodeXml  the xml representation of the node
     * @return the information of its objects of the given type as {@link IliasNode}
     * @throws JDOMException
     * @throws IOException
     */
    public static List<IliasNode> parseRefIdsOfNodeType(final IliasNode.Type nodeType, final String nodeXml) throws JDOMException, IOException {
        Document doc = createSaxDocFromString(nodeXml);

        Element rootElement = doc.getRootElement();
        List<Element> objects = rootElement.getChildren("Object");
        return objects.stream()
                .filter(o -> isOfNodeType(nodeType, o))
                .map(o -> new IliasNode(Integer.valueOf(o.getChild("References").getAttribute("ref_id").getValue().trim()).intValue(),
                                        nodeType,
                                        o.getChild("Title").getValue().trim()))
                .collect(Collectors.toList());
    }

    /**
     * Determines if an element is of a specific {@link com.github.mavogel.ilias.model.IliasNode.Type}
     *
     * @param nodeType the type of the node to compare to
     * @param element  the element in the xml tree
     * @return <code>true</code> if the element is a folder, <code>false</code> otherwise
     */
    private static boolean isOfNodeType(final IliasNode.Type nodeType, final Element element) {
        return nodeType.getXmlShortName()
                .equalsIgnoreCase(String.valueOf(element.getAttribute("type").getValue().trim()));
    }

    /**
     * Parses the member ids from a group xml.
     *
     * @param groupXml the group xml
     * @return the ids of the members of the group
     * @throws JDOMException if no document for the xml parser could be created
     * @throws IOException   if no InputStream could be created from the xmlString
     */
    public static List<Integer> parseGroupMemberIds(final String groupXml) throws JDOMException, IOException {
        Document doc = createSaxDocFromString(groupXml);

        Element rootElement = doc.getRootElement();
        List<Element> members = rootElement.getChildren("member");
        return members.stream()
                .map(m -> m.getAttribute("id").getValue())
                .map(XMLUtils::extractId)
                .collect(Collectors.toList());
    }

    /**
     * Extracts the id from an idString<br>
     * The idString is expected to have the format 'il_0_usr_<member_id>'. Example: 'il_0_usr_184191'
     *
     * @param idString the idString
     * @return the id
     */
    private static int extractId(final String idString) {
        return Integer.valueOf(idString.substring(idString.lastIndexOf('_') + 1)).intValue();
    }

    /**
     * Sets the new registration start and end dates in the group xml and returns the update xml.
     *
     * @param groupXml          the groupXml
     * @param registrationStart the new registration start
     * @param registrationEnd   the new registration end
     * @return the updated groupXml
     * @throws JDOMException if no document for the xml parser could be created
     * @throws IOException   if no InputStream could be created from the xmlString
     */
    public static String setRegistrationDates(final String groupXml,
                                              final long registrationStart, final long registrationEnd) throws JDOMException, IOException {
        Document doc = createSaxDocFromString(groupXml);

        Element rootElement = doc.getRootElement();
        Element temporarilyAvailable = rootElement.getChild("registration").getChild("temporarilyAvailable");
        temporarilyAvailable.getChild("start").setText(String.valueOf(registrationStart));
        temporarilyAvailable.getChild("end").setText(String.valueOf(registrationEnd));
        return new XMLOutputter().outputString(doc);
    }
}
