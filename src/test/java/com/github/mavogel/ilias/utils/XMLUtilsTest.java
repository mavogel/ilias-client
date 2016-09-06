package com.github.mavogel.ilias.utils;

import com.github.mavogel.ilias.model.IliasNode;
import org.jdom.JDOMException;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public void shouldParseOneCourseRefId() throws IOException, JDOMException, ParserConfigurationException {
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
    public void shouldParseNoCourseRefId() throws IOException, JDOMException, ParserConfigurationException {
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
    public void shouldParseMultipleCourseRefIds() throws IOException, JDOMException, ParserConfigurationException {
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
    public void shouldParseFourFolderRefIdsFromNode() throws IOException, JDOMException, ParserConfigurationException {
        // == prepare
        final String testFile = TEST_RES_DIR + "treeNodesWithFoldersAndWebRefs.xml";
        final String nodeXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<Integer> folderRefIds = XMLUtils.parseRefIdsOfNodeType(IliasNode.Type.FOLDER, nodeXml);

        // == verify
        assertTrue(folderRefIds != null);
        assertEquals(new ArrayList<>(Arrays.asList(44528, 44529, 44527, 44530)), folderRefIds);
    }

    @Test
    public void shouldParse36GroupRefIdsFromNode() throws IOException, JDOMException, ParserConfigurationException {
        // == prepare
        final String testFile = TEST_RES_DIR + "treeNodesWithGroups.xml";
        final String nodeXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<Integer> groupRefIds = XMLUtils.parseRefIdsOfNodeType(IliasNode.Type.GROUP, nodeXml);

        // == verify
        assertTrue(groupRefIds != null);
        assertEquals(36, groupRefIds.size());
    }

    @Test
    public void shouldParse36GroupRefIdsFromNodeWithFoldersAndWebRefs() throws IOException, JDOMException, ParserConfigurationException {
        // == prepare
        final String testFile = TEST_RES_DIR + "treeNodesWithGroupsFoldersWebRefs.xml";
        final String nodeXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<Integer> groupRefIds = XMLUtils.parseRefIdsOfNodeType(IliasNode.Type.GROUP, nodeXml);

        // == verify
        assertTrue(groupRefIds != null);
        assertEquals(36, groupRefIds.size());
        assertTrue(groupRefIds.contains(166848));
        assertTrue(groupRefIds.contains(146462));
        assertFalse(groupRefIds.contains(44528));
    }

    @Test
    public void shouldParseFileRefIdsFromNode() throws IOException, JDOMException, ParserConfigurationException {
        // == prepare
        final String testFile = TEST_RES_DIR + "treeNodesWithFiles.xml";
        final String nodeXml = Files.lines(Paths.get(testFile)).collect(Collectors.joining());

        // == go
        List<Integer> folderRefIds = XMLUtils.parseRefIdsOfNodeType(IliasNode.Type.FILE, nodeXml);

        // == verify
        assertTrue(folderRefIds != null);
        assertEquals(new ArrayList<>(Arrays.asList(203095)), folderRefIds);
    }
}
