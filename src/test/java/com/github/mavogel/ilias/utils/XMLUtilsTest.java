/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016 Manuel Vogel
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 *  https://opensource.org/licenses/MIT
 */
package com.github.mavogel.ilias.utils;

import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.IliasUser;
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
    public void shouldEmptyListBecauseGroupHasNoMembers() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "groupInfoNoMembers.xml";
        final String groupXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<Integer> memberIds = XMLUtils.parseGroupMemberIds(groupXml);

        // == verify
        assertTrue(memberIds != null);
        assertTrue(memberIds.isEmpty());
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

    @Test
    public void shouldActivateAndSetNewRegistrationDatesOnGroup() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "groupInfoXMLWithoutRegPeriod.xml";
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

    @Test
    public void shouldParseMemberRoleIdFromGroupXml() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "localRolesForGroup.xml";
        final String localRolesFromGroupXML = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        int memberRoleId = XMLUtils.parseGroupMemberRoleId(localRolesFromGroupXML);

        // == verify
        assertEquals(215170, memberRoleId);
    }

    @Test
    public void shouldParseOneUserRecordFromRole() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "getUserForMemberRole.xml";
        final String rolesXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<IliasUser> userRecords = XMLUtils.parseIliasUserRecordsFromRole(rolesXml);

        // == verify
        assertEquals(1, userRecords.size());
        assertEquals(new IliasUser("Dave", "Werner", "d.werner@my-company.com"), userRecords.get(0));
    }

    @Test
    public void shouldParseMultipleUserRecordsFromRole() throws IOException, JDOMException {
        // == prepare
        final String testFile = TEST_RES_DIR + "getMultipleUsersForMemberRole.xml";
        final String rolesXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<IliasUser> userRecords = XMLUtils.parseIliasUserRecordsFromRole(rolesXml);

        // == verify
        assertEquals(2, userRecords.size());
        assertEquals(new IliasUser("Dave", "Werner", "d.werner@my-company.com"), userRecords.get(0));
        assertEquals(new IliasUser("Ashley", "Miller", "a.miller@my-company.com"), userRecords.get(1));
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
