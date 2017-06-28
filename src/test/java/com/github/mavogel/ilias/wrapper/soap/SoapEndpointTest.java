package com.github.mavogel.ilias.wrapper.soap;/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2017 Manuel Vogel
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

import com.github.mavogel.client.ILIASSoapWebserviceBindingStub;
import com.github.mavogel.client.ILIASSoapWebserviceLocator;
import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.GroupUserModelFull;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.IliasUser;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.utils.Defaults;
import com.github.mavogel.ilias.wrapper.AbstractIliasEndpoint;
import com.github.mavogel.ilias.wrapper.DisplayStatus;
import com.github.mavogel.ilias.wrapper.PermissionOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by mavogel on 6/26/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SoapEndpoint.class, AbstractIliasEndpoint.class,
        ILIASSoapWebservicePortType.class, ILIASSoapWebserviceLocator.class,
        SoapXMLUtils.class})
public class SoapEndpointTest {

    private static final String SID = "sid-123";
    private static final int USER_ID = 5678;
    private static final int MAX_FOLDER_DEPTH = 2;

    private SoapEndpoint classUnderTest;
    private ILIASSoapWebservicePortType endPointMock;

    @Before
    public void setUp() throws Exception {
        // == prepare
        final LoginConfiguration loginConfiguration = LoginConfiguration.asLDAPLogin("myEndpoint", "client_id", "user", "pwd", MAX_FOLDER_DEPTH);

        // == train
        final ILIASSoapWebserviceLocator locatorMock = PowerMockito.mock(ILIASSoapWebserviceLocator.class);
        PowerMockito.whenNew(ILIASSoapWebserviceLocator.class).withNoArguments().thenReturn(locatorMock);
        PowerMockito.doNothing().when(locatorMock, "setILIASSoapWebservicePortEndpointAddress", loginConfiguration.getEndpoint());
        endPointMock = PowerMockito.mock(ILIASSoapWebserviceBindingStub.class);
        PowerMockito.when(locatorMock.getILIASSoapWebservicePort()).thenReturn(endPointMock);

        PowerMockito.when(endPointMock.loginLDAP(loginConfiguration.getClient(), loginConfiguration.getUsername(), loginConfiguration.getPassword()))
                .thenReturn(SID);
        PowerMockito.when(endPointMock.getUserIdBySid(SID)).thenReturn(USER_ID);

        // == set
        classUnderTest = new SoapEndpoint(loginConfiguration);
    }

    @Test
    public void shouldLogout() throws Exception {
        // == train
        PowerMockito.when(endPointMock.logout(SID)).thenReturn(true);

        // == go
        classUnderTest.logout();

        // == verify
        Mockito.verify(endPointMock).logout(SID);
    }

    @Test
    public void shouldGetCoursesForUser() throws Exception {
        // == prepare
        String foundCourses = "foundCourses";
        List<Integer> courseRefIds = Arrays.asList(1, 2, 3);

        // == train
        PowerMockito.when(endPointMock.getCoursesForUser(SID, "courseXML"))
                .thenReturn(foundCourses);
        PowerMockito.mockStatic(SoapXMLUtils.class);
        PowerMockito.when(SoapXMLUtils.createCoursesResultXml(USER_ID, DisplayStatus.ADMIN)).thenReturn("courseXML");
        PowerMockito.when(SoapXMLUtils.parseCourseRefIds(foundCourses)).thenReturn(courseRefIds);

        for (int courseRefId : courseRefIds) {
            PowerMockito.when(endPointMock.getCourseXML(SID, courseRefId)).thenReturn("Course " + courseRefId +  " XML");
            PowerMockito.when(SoapXMLUtils.createsFromCourseNodeInfo(Mockito.eq(courseRefId), Mockito.anyString())).thenReturn(new IliasNode(courseRefId, IliasNode.Type.COURSE, "Course " + courseRefId));
        }

        // == go
        List<IliasNode> coursesForUser = classUnderTest.getCoursesForUser(DisplayStatus.ADMIN);

        // == verify
        assertEquals(3, coursesForUser.size());
        Mockito.verify(endPointMock).getCoursesForUser(Mockito.eq(SID), Mockito.anyString());
        PowerMockito.verifyStatic(Mockito.times(1));
        SoapXMLUtils.createCoursesResultXml(USER_ID, DisplayStatus.ADMIN);
        PowerMockito.verifyStatic(Mockito.times(1));
        SoapXMLUtils.parseCourseRefIds(foundCourses);
        Mockito.verify(endPointMock, Mockito.times(courseRefIds.size())).getCourseXML(Mockito.eq(SID), Mockito.anyInt());
        PowerMockito.verifyStatic(Mockito.times(courseRefIds.size()));
        SoapXMLUtils.createsFromCourseNodeInfo(Mockito.anyInt(), Mockito.anyString());
    }

    @Test
    public void shouldGet3GroupsFrom2FoldersFromCourse() throws Exception {
        // == prepare
        List<IliasNode> foundFolders = Arrays.asList(
                new IliasNode(1, IliasNode.Type.FOLDER, "Folder 1"),
                new IliasNode(2, IliasNode.Type.FOLDER, "Folder 2")
        );
        final List<IliasNode> folder1Groups = Arrays.asList(new IliasNode(3, IliasNode.Type.GROUP, "Group 1"));
        final List<IliasNode> folder2Groups = Arrays.asList(new IliasNode(4, IliasNode.Type.GROUP, "Group 2"));

        // == train
        PowerMockito.when(endPointMock.getTreeChilds(SID, 0, IliasNode.Type.compose(IliasNode.Type.FOLDER), USER_ID))
                .thenReturn("courseXML");
        PowerMockito.when(endPointMock.getTreeChilds(SID, 1, IliasNode.Type.compose(IliasNode.Type.FOLDER), USER_ID))
                .thenReturn("folder1XML");
        PowerMockito.when(endPointMock.getTreeChilds(SID, 2, IliasNode.Type.compose(IliasNode.Type.FOLDER), USER_ID))
                .thenReturn("folder2XML");
        PowerMockito.mockStatic(SoapXMLUtils.class);
        PowerMockito.when(SoapXMLUtils.parseRefIdsOfNodeType(IliasNode.Type.FOLDER, "courseXML"))
                .thenReturn(foundFolders);
        PowerMockito.when(SoapXMLUtils.parseRefIdsOfNodeType(IliasNode.Type.FOLDER, "folder1XML"))
                .thenReturn(Collections.emptyList());
        PowerMockito.when(SoapXMLUtils.parseRefIdsOfNodeType(IliasNode.Type.FOLDER, "folder2XML"))
                .thenReturn(Collections.emptyList());
        PowerMockito.when(endPointMock.getTreeChilds(SID, 1, IliasNode.Type.compose(IliasNode.Type.GROUP), USER_ID))
                .thenReturn("group1XML");
        PowerMockito.when(endPointMock.getTreeChilds(SID, 2, IliasNode.Type.compose(IliasNode.Type.GROUP), USER_ID))
                .thenReturn("group2XML");
        PowerMockito.when(SoapXMLUtils.parseRefIdsOfNodeType(IliasNode.Type.GROUP, "group1XML"))
                .thenReturn(folder1Groups);
        PowerMockito.when(SoapXMLUtils.parseRefIdsOfNodeType(IliasNode.Type.GROUP, "group2XML"))
                .thenReturn(folder2Groups);


        // == go
        List<IliasNode> groupsFromCourse = classUnderTest.getGroupsFromCourse(new IliasNode(0, IliasNode.Type.COURSE, "My Course"));

        // == verify
        assertEquals(2, groupsFromCourse.size());
        Mockito.verify(endPointMock).getTreeChilds(SID, 0, IliasNode.Type.compose(IliasNode.Type.FOLDER), USER_ID);
        Mockito.verify(endPointMock).getTreeChilds(SID, 1, IliasNode.Type.compose(IliasNode.Type.FOLDER), USER_ID);
        Mockito.verify(endPointMock).getTreeChilds(SID, 2, IliasNode.Type.compose(IliasNode.Type.FOLDER), USER_ID);
        PowerMockito.verifyStatic(Mockito.times(1));
        SoapXMLUtils.parseRefIdsOfNodeType(IliasNode.Type.FOLDER, "courseXML");
        PowerMockito.verifyStatic(Mockito.times(1));
        SoapXMLUtils.parseRefIdsOfNodeType(IliasNode.Type.FOLDER, "folder1XML");
        PowerMockito.verifyStatic(Mockito.times(1));
        SoapXMLUtils.parseRefIdsOfNodeType(IliasNode.Type.FOLDER, "folder2XML");
        Mockito.verify(endPointMock).getTreeChilds(SID, 1, IliasNode.Type.compose(IliasNode.Type.GROUP), USER_ID);
        PowerMockito.verifyStatic(Mockito.times(1));
        SoapXMLUtils.parseRefIdsOfNodeType(IliasNode.Type.GROUP, "group1XML");
        Mockito.verify(endPointMock).getTreeChilds(SID, 2, IliasNode.Type.compose(IliasNode.Type.GROUP), USER_ID);
        PowerMockito.verifyStatic(Mockito.times(1));
        SoapXMLUtils.parseRefIdsOfNodeType(IliasNode.Type.GROUP, "group2XML");
    }

    @Test
    public void shouldGrantFileUploadPermissionForMembersForOneGroupAndFailOnSecond() throws Exception {
        // == prepare
        List<IliasNode> groups = Arrays.asList(
                new IliasNode(1, IliasNode.Type.GROUP, "Group 1"),
                new IliasNode(2, IliasNode.Type.GROUP, "Group 2"),
                new IliasNode(3, IliasNode.Type.GROUP, "Group 3")
        );

        int[] allPermissions = PermissionOperation.build(PermissionOperation.VISIBLE,
                PermissionOperation.READ,
                PermissionOperation.JOIN,
                PermissionOperation.LEAVE,
                PermissionOperation.CREATE_FILE);

        // == train
        PowerMockito.when(endPointMock.getLocalRoles(SID, 1)).thenReturn("group1RolesXML");
        PowerMockito.when(endPointMock.getLocalRoles(SID, 2)).thenReturn("group2RolesXML");
        PowerMockito.when(endPointMock.getLocalRoles(SID, 3)).thenThrow(new RemoteException());
        PowerMockito.mockStatic(SoapXMLUtils.class);
        PowerMockito.when(SoapXMLUtils.parseGroupMemberRoleId("group1RolesXML")).thenReturn(11);
        PowerMockito.when(SoapXMLUtils.parseGroupMemberRoleId("group2RolesXML")).thenReturn(22);
        PowerMockito.when(endPointMock.grantPermissions(SID, 1, 11, allPermissions)).thenReturn(true);
        PowerMockito.when(endPointMock.grantPermissions(SID, 2, 22, allPermissions)).thenReturn(false);

        // == go
        classUnderTest.grantFileUploadPermissionForMembers(groups);

        // == verify
        Mockito.verify(endPointMock).getLocalRoles(SID, 1);
        Mockito.verify(endPointMock).getLocalRoles(SID, 2);
        Mockito.verify(endPointMock).getLocalRoles(SID, 3);
        PowerMockito.verifyStatic(Mockito.times(1));
        SoapXMLUtils.parseGroupMemberRoleId("group1RolesXML");
        SoapXMLUtils.parseGroupMemberRoleId("group2RolesXML");
        Mockito.verify(endPointMock).grantPermissions(SID, 1, 11, allPermissions);
        Mockito.verify(endPointMock).grantPermissions(SID, 2, 22, allPermissions);
    }

    @Test
    public void shouldGet5UsersFrom2Groups() throws Exception {
        // == prepare
        List<IliasNode> groups = Arrays.asList(
                new IliasNode(1, IliasNode.Type.GROUP, "Group 1"),
                new IliasNode(2, IliasNode.Type.GROUP, "Group 2")
        );
        final List<IliasUser> group1UserRecords = Arrays.asList(
                new IliasUser("firstname1", "lastname1", "mail1"),
                new IliasUser("firstname2", "lastname2", "mail2"),
                new IliasUser("firstname3", "lastname3", "mail3")
        );
        final List<IliasUser> group2UserRecords = Arrays.asList(
                new IliasUser("firstname4", "lastname4", "mail4"),
                new IliasUser("firstname5", "lastname5", "mail5"),
                new IliasUser("firstname6", "lastname6", "mail6"),
                new IliasUser("firstname7", "lastname7", "mail7")
        );

        // == train
        PowerMockito.when(endPointMock.getLocalRoles(SID, 1)).thenReturn("group1RolesXML");
        PowerMockito.when(endPointMock.getLocalRoles(SID, 2)).thenReturn("group2RolesXML");
        PowerMockito.mockStatic(SoapXMLUtils.class);
        PowerMockito.when(SoapXMLUtils.parseGroupMemberRoleId("group1RolesXML")).thenReturn(11);
        PowerMockito.when(SoapXMLUtils.parseGroupMemberRoleId("group2RolesXML")).thenReturn(22);
        PowerMockito.when(endPointMock.getUsersForRole(SID, 11, Defaults.ATTACH_ROLES, Defaults.IS_ACTIVE))
            .thenReturn("usersForRole11XML");
        PowerMockito.when(endPointMock.getUsersForRole(SID, 22, Defaults.ATTACH_ROLES, Defaults.IS_ACTIVE))
                .thenReturn("usersForRole22XML");
        PowerMockito.when(SoapXMLUtils.parseIliasUserRecordsFromRole("usersForRole11XML"))
                .thenReturn(group1UserRecords);
        PowerMockito.when(SoapXMLUtils.parseIliasUserRecordsFromRole("usersForRole22XML"))
                .thenReturn(group2UserRecords);


        // == go
        List<GroupUserModelFull> groupUserModels = classUnderTest.getUsersForGroups(groups);

        // == verify
        assertEquals(2, groupUserModels.size());
        assertEquals(3, groupUserModels.get(0).getGroupMembers().size());
        assertEquals(4, groupUserModels.get(1).getGroupMembers().size());
    }

    @Test
    public void shouldGet3FilesFrom3Groups() throws Exception {
        // == prepare
        List<IliasNode> groups = Arrays.asList(
                new IliasNode(1, IliasNode.Type.GROUP, "Group 1"),
                new IliasNode(2, IliasNode.Type.GROUP, "Group 2"),
                new IliasNode(3, IliasNode.Type.GROUP, "Group 3")

        );
        List<IliasNode> group1Files = Arrays.asList(
                new IliasNode(11, IliasNode.Type.FILE, "File 1")
        );
        List<IliasNode> group2Files = Arrays.asList(
                new IliasNode(22, IliasNode.Type.FILE, "File 2"),
                new IliasNode(23, IliasNode.Type.FILE, "File 3")
        );

        // == train
        PowerMockito.when(endPointMock.getTreeChilds(SID, 1, IliasNode.Type.compose(IliasNode.Type.FILE), USER_ID))
                .thenReturn("group1XML");
        PowerMockito.when(endPointMock.getTreeChilds(SID, 2, IliasNode.Type.compose(IliasNode.Type.FILE), USER_ID))
                .thenReturn("group2XML");
        PowerMockito.when(endPointMock.getTreeChilds(SID, 3, IliasNode.Type.compose(IliasNode.Type.FILE), USER_ID))
                .thenReturn("group3XML");
        PowerMockito.mockStatic(SoapXMLUtils.class);
        PowerMockito.when(SoapXMLUtils.parseRefIdsOfNodeType(IliasNode.Type.FILE, "group1XML"))
                .thenReturn(group1Files);
        PowerMockito.when(SoapXMLUtils.parseRefIdsOfNodeType(IliasNode.Type.FILE, "group2XML"))
                .thenReturn(group2Files);
        PowerMockito.when(SoapXMLUtils.parseRefIdsOfNodeType(IliasNode.Type.FILE, "group3XML"))
                .thenReturn(Collections.emptyList());


        // == go
        List<IliasNode> files = classUnderTest.getFilesFromGroups(groups);

        // == verify
        assertEquals(3, files.size());
    }

    @Test
    public void shouldDelete1ObjectNodeAndFailOnOthers() throws Exception {
        // == prepare
        List<IliasNode> files = Arrays.asList(
                new IliasNode(1, IliasNode.Type.FILE, "File 1"),
                new IliasNode(2, IliasNode.Type.FILE, "File 2"),
                new IliasNode(3, IliasNode.Type.FILE, "File 3")
        );

        // == train
        PowerMockito.when(endPointMock.deleteObject(SID, 1)).thenReturn(true);
        PowerMockito.when(endPointMock.deleteObject(SID, 2)).thenReturn(false);
        PowerMockito.when(endPointMock.deleteObject(SID, 3)).thenThrow(new RemoteException("failed"));

        // == go
        classUnderTest.deleteObjectNodes(files);

        // == verify
        Mockito.verify(endPointMock).deleteObject(SID, 1);
        Mockito.verify(endPointMock).deleteObject(SID, 2);
        Mockito.verify(endPointMock).deleteObject(SID, 3);
    }

    @Test
    public void shouldRemoveMembersFrom3GroupsAndFailOn1Group() throws Exception {
        // == prepare
        List<IliasNode> groups = Arrays.asList(
                new IliasNode(1, IliasNode.Type.GROUP, "Group 1"),
                new IliasNode(2, IliasNode.Type.GROUP, "Group 2"),
                new IliasNode(3, IliasNode.Type.GROUP, "Group 3")

        );
        // == train
        PowerMockito.when(endPointMock.getGroup(SID, 1)).thenReturn("group1XML");
        PowerMockito.when(endPointMock.getGroup(SID, 2)).thenReturn("group2XML");
        PowerMockito.when(endPointMock.getGroup(SID, 3)).thenReturn("group3XML");

        PowerMockito.mockStatic(SoapXMLUtils.class);
        PowerMockito.when(SoapXMLUtils.parseGroupMemberIds("group1XML"))
                .thenReturn(Arrays.asList(11));
        PowerMockito.when(SoapXMLUtils.parseGroupMemberIds("group2XML"))
                .thenReturn(Arrays.asList(22, 23));
        PowerMockito.when(SoapXMLUtils.parseGroupMemberIds("group3XML"))
                .thenReturn(Collections.emptyList());

        PowerMockito.when(endPointMock.excludeGroupMember(SID, 1, 11))
                .thenReturn(true);
        PowerMockito.when(endPointMock.excludeGroupMember(SID, 2, 22))
                .thenReturn(true);
        PowerMockito.when(endPointMock.excludeGroupMember(SID, 2, 23))
                .thenReturn(false);

        // == go
        classUnderTest.removeAllMembersFromGroups(groups);

        // == verify
        Mockito.verify(endPointMock).getGroup(SID, 1);
        Mockito.verify(endPointMock).getGroup(SID, 2);
        Mockito.verify(endPointMock).getGroup(SID, 3);
        Mockito.verify(endPointMock).excludeGroupMember(SID, 1, 11);
        Mockito.verify(endPointMock).excludeGroupMember(SID, 2, 22);
        Mockito.verify(endPointMock).excludeGroupMember(SID, 2, 23);
        Mockito.verify(endPointMock, Mockito.never()).excludeGroupMember(
                Mockito.eq(SID), Mockito.eq(3), Mockito.anyInt());
    }

}
