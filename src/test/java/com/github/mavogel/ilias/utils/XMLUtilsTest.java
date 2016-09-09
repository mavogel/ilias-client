package com.github.mavogel.ilias.utils;

import com.github.mavogel.ilias.model.IliasNode;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mavogel on 9/5/16.
 */
public class XMLUtilsTest {

    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String BASE_DIR = System.getProperty("user.dir");
    private static final String TEST_RES_DIR = BASE_DIR + FILE_SEP + "src" + FILE_SEP + "test" + FILE_SEP + "resources" + FILE_SEP;

    @Test
    public void shouldHandleOneDisplayStatus() {
        // == prepare
        String expected = new StringBuilder()
                .append("<result><colspecs><colspec name=\"user_id\"/><colspec name=\"status\"/></colspecs><row><column>")
                .append(122).append("</column><column>")
                .append(4)
                .append("</column></row></result>").toString();

        // == go
        String adminCoursesResultXml = XMLUtils.createCoursesResultXml(122, IliasUtils.DisplayStatus.ADMIN);

        // == verify
        assertEquals(expected, adminCoursesResultXml);
    }

    @Test
    public void shouldHandleMoreDisplayStati() {
        // == prepare
        String expected = new StringBuilder()
                .append("<result><colspecs><colspec name=\"user_id\"/><colspec name=\"status\"/></colspecs><row><column>")
                .append(122).append("</column><column>")
                .append(12)
                .append("</column></row></result>").toString();

        // == go
        String adminCoursesResultXml = XMLUtils.createCoursesResultXml(122, IliasUtils.DisplayStatus.ADMIN,
                IliasUtils.DisplayStatus.OWNER);

        // == verify
        assertEquals(expected, adminCoursesResultXml);
    }

    @Test
    public void shouldParseOneCourseRefId() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "selectedCourses.xml";
        final String courseXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<Integer> courseRefIds = XMLUtils.parseCourseRefIds(courseXml);

        // == verify
        assertTrue(courseRefIds != null);
        assertEquals(44525, courseRefIds.get(0).intValue());
    }

    @Test
    public void shouldParseNoCourseRefId() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "selectedCoursesEmpty.xml";
        final String courseXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<Integer> courseRefIds = XMLUtils.parseCourseRefIds(courseXml);

        // == verify
        assertTrue(courseRefIds != null);
        assertTrue(courseRefIds.isEmpty());
    }

    @Test
    public void shouldParseMultipleCourseRefIds() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "selectedMultipleCourses.xml";
        final String courseXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<Integer> courseRefIds = XMLUtils.parseCourseRefIds(courseXml);

        // == verify
        assertTrue(courseRefIds != null);
        assertEquals(new ArrayList<>(Arrays.asList(29651, 30143)), courseRefIds);
    }

    @Test
    public void shouldCreateAnIliasNoteFromCourseInfoXml() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "courseInfo.xml";
        final String courseXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());
        final int courseRefId = 13345;

        // == go
        IliasNode iliasNode = XMLUtils.createsFromCourseNodeInfo(courseRefId, courseXml);

        // == verify
        assertEquals(13345, iliasNode.getRefId());
        assertEquals(IliasNode.Type.COURSE, iliasNode.getNodeType());
        assertEquals("CS 134 Databases 1", iliasNode.getTitle());
    }

    @Test
    public void shouldParseFourFolderRefIdsFromNode() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "treeNodesWithFoldersAndWebRefs.xml";
        final String nodeXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<IliasNode> folderNodes = XMLUtils.parseRefIdsOfNodeType(IliasNode.Type.FOLDER, nodeXml);

        // == verify
        assertTrue(folderNodes != null);
        List<IliasNode> expectedNode = new ArrayList<>();
        expectedNode.add(new IliasNode(44528, IliasNode.Type.FOLDER, "Folder1"));
        expectedNode.add(new IliasNode(44529, IliasNode.Type.FOLDER, "Exams"));
        expectedNode.add(new IliasNode(44527, IliasNode.Type.FOLDER, "Scripts"));
        expectedNode.add(new IliasNode(44530, IliasNode.Type.FOLDER, "Lecture Materials"));
        assertEquals(expectedNode.toString(), folderNodes.toString());
    }

    @Test
    public void shouldParse36GroupRefIdsFromNode() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "treeNodesWithGroups.xml";
        final String nodeXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<IliasNode> groupNodes = XMLUtils.parseRefIdsOfNodeType(IliasNode.Type.GROUP, nodeXml);

        // == verify
        assertTrue(groupNodes != null);
        assertEquals(36, groupNodes.size());
    }

    @Test
    public void shouldParse36GroupRefIdsFromNodeWithFoldersAndWebRefs() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "treeNodesWithGroupsFoldersWebRefs.xml";
        final String nodeXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<IliasNode> groupNodes = XMLUtils.parseRefIdsOfNodeType(IliasNode.Type.GROUP, nodeXml);

        // == verify
        assertTrue(groupNodes != null);
        assertEquals(36, groupNodes.size());
    }

    @Test
    public void shouldParseFileRefIdsFromNode() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "treeNodesWithFiles.xml";
        final String nodeXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<IliasNode> folderNodes = XMLUtils.parseRefIdsOfNodeType(IliasNode.Type.FILE, nodeXml);

        // == verify
        assertTrue(folderNodes != null);
        List<IliasNode> expectedNodes = new ArrayList<>();
        expectedNodes.add(new IliasNode(203095, IliasNode.Type.FILE, "MyFile.sql"));
        assertEquals(expectedNodes.toString(), folderNodes.toString());
    }

    @Test
    public void shouldParse3MemberIdsGroupInfo() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "groupInfo.xml";
        final String groupXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<Integer> memberIds = XMLUtils.parseGroupMemberIds(groupXml);

        // == verify
        assertTrue(memberIds != null);
        assertEquals(new ArrayList<>(Arrays.asList(184191, 206011, 206074)), memberIds);
    }

    @Test
    public void shouldSetNewRegistrationDatesOnGroup() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "groupInfo.xml";
        final String groupXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        final long registrationStart = LocalDateTime.of(2014, Month.APRIL, 21, 14, 00).toEpochSecond(ZoneOffset.UTC);
        final long registrationEnd = LocalDateTime.of(2014, Month.APRIL, 23, 14, 00).toEpochSecond(ZoneOffset.UTC);

        // == go
        String updatedGroupXml = XMLUtils.setRegistrationDates(groupXml, registrationStart, registrationEnd);

        // == verify
        Document expectedDoc = createDocFromXml(updatedGroupXml);
        Element rootElement = expectedDoc.getRootElement();
        Element temporarilyAvailable = rootElement.getChild("registration").getChild("temporarilyAvailable");

        assertEquals(registrationStart, Long.valueOf(temporarilyAvailable.getChild("start").getText()).longValue());
        assertEquals(registrationEnd, Long.valueOf(temporarilyAvailable.getChild("end").getText()).longValue());
    }

    /**
     * Creates a {@link Document} from an xmlString
     *
     * @param xmlString the xml string
     * @return the document
     * @throws JDOMException
     * @throws IOException
     */
    private Document createDocFromXml(final String xmlString) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(new ByteArrayInputStream(xmlString.getBytes()));
    }
}
