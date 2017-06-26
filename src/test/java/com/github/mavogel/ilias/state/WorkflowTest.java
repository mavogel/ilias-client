package com.github.mavogel.ilias.state;/*
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

import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.utils.IOUtils;
import com.github.mavogel.ilias.wrapper.AbstractIliasEndpoint;
import com.github.mavogel.ilias.wrapper.DisplayStatus;
import com.github.mavogel.ilias.wrapper.EndpointBuilder;
import com.github.mavogel.ilias.wrapper.soap.SoapEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by mavogel on 6/21/17.
 * <p>
 * Simulates complete workflows by mocking the ilias backend calls.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ToolStateMachine.class, WorkflowTest.class, IOUtils.class,
        AbstractIliasEndpoint.class, SoapEndpoint.class, EndpointBuilder.class})
public class WorkflowTest {

    private LoginConfiguration loginConfiguration;
    private SoapEndpoint endpointMock;
    private UserDataIds userDateIds;
//    private ToolStateMachine toolStateMachineMock;

    @Before
    public void setUp() throws Exception {
        createEndpoint();
    }

    @Test
    public void shouldFindNoCoursesAndQuitThen() throws Exception {
        // == prepare
        final List<IliasNode> noCourses = Collections.emptyList();

        // == train
        PowerMockito.when(endpointMock.getCoursesForUser(DisplayStatus.ADMIN)).thenReturn(noCourses);
        PowerMockito.when(IOUtils.readAndParseSingleChoiceFromUser(Mockito.anyList())).thenReturn(0, 2);

        // == go
        createToolStateMachine().start();

        // == verify
        Mockito.verify(endpointMock, Mockito.times(2)).getCoursesForUser(DisplayStatus.ADMIN);
        PowerMockito.verifyStatic(Mockito.times(2));
        IOUtils.readAndParseSingleChoiceFromUser(Mockito.anyList());
    }

    @Test
    public void shouldOneNCoursesAndQuitThen() throws Exception {
        // == prepare
        final List<IliasNode> courses = Arrays.asList(new IliasNode(1, IliasNode.Type.COURSE, "My Course"));

        // == train
        PowerMockito.when(endpointMock.getCoursesForUser(DisplayStatus.ADMIN)).thenReturn(courses);
        PowerMockito.when(IOUtils.readAndParseSingleChoiceFromUser(Mockito.anyList())).thenReturn(0, 2);

        // == go
        createToolStateMachine().start();

        // == verify
        Mockito.verify(endpointMock, Mockito.times(1)).getCoursesForUser(DisplayStatus.ADMIN);
        PowerMockito.verifyStatic(Mockito.times(2));
        IOUtils.readAndParseSingleChoiceFromUser(Mockito.anyList());
    }

    @Test
    public void shouldFind3GroupsInOneNCoursesAndRemoveUsers() throws Exception {
        // == prepare
        final List<IliasNode> courses = Arrays.asList(new IliasNode(1, IliasNode.Type.COURSE, "My Course"));
        final List<IliasNode> groups = Arrays.asList(
                new IliasNode(11, IliasNode.Type.GROUP, "Group 1"),
                new IliasNode(12, IliasNode.Type.GROUP, "Group 2"),
                new IliasNode(13, IliasNode.Type.GROUP, "Group 3"));

        // == train
        PowerMockito.when(endpointMock.getCoursesForUser(DisplayStatus.ADMIN)).thenReturn(courses);
        PowerMockito.when(endpointMock.getGroupRefIdsFromCourses(courses.get(0))).thenReturn(groups);
        // 0: choose course; 1: actionsOnGroups; 1 quit
        PowerMockito.when(IOUtils.readAndParseSingleChoiceFromUser(Mockito.anyList())).thenReturn(0, 1, 1);

        PowerMockito.when(IOUtils.readAndParseChoicesFromUser(Mockito.anyList()))
                // 0,1,2: choosen groups;
                .thenReturn(Arrays.asList(0, 1, 2))
                // 1: remove users action
                .thenReturn(Arrays.asList(1));

        PowerMockito.when(IOUtils.readAndParseUserConfirmation()).thenReturn(true);
        PowerMockito.doNothing().when(endpointMock, "removeAllMembersFromGroups", groups);

        // == go
        createToolStateMachine().start();

        // == verify
        Mockito.verify(endpointMock, Mockito.times(1)).getCoursesForUser(DisplayStatus.ADMIN);
        PowerMockito.verifyStatic(Mockito.times(3));
        IOUtils.readAndParseSingleChoiceFromUser(Mockito.anyList());
        PowerMockito.verifyStatic(Mockito.times(2));
        IOUtils.readAndParseChoicesFromUser(Mockito.anyList());
        Mockito.verify(endpointMock, Mockito.times(1)).removeAllMembersFromGroups(groups);
    }

    @Test
    public void shouldFind4GroupsInOneNCoursesAndRemoveUsersAndUploadedMaterial() throws Exception {
        // == prepare
        final List<IliasNode> courses = Arrays.asList(new IliasNode(1, IliasNode.Type.COURSE, "My Course"));
        final List<IliasNode> groups = Arrays.asList(
                new IliasNode(11, IliasNode.Type.GROUP, "Group 1"),
                new IliasNode(12, IliasNode.Type.GROUP, "Group 2"),
                new IliasNode(13, IliasNode.Type.GROUP, "Group 3"),
                new IliasNode(14, IliasNode.Type.GROUP, "Group 4"));
        final List<IliasNode> files = Arrays.asList(
                new IliasNode(21, IliasNode.Type.FILE, "File 1"),
                new IliasNode(22, IliasNode.Type.FILE, "File 2"));

        // == train
        PowerMockito.when(endpointMock.getCoursesForUser(DisplayStatus.ADMIN)).thenReturn(courses);
        PowerMockito.when(endpointMock.getGroupRefIdsFromCourses(courses.get(0))).thenReturn(groups);
        // 0: choose course; 1: actionsOnGroups; 1 quit
        PowerMockito.when(IOUtils.readAndParseSingleChoiceFromUser(Mockito.anyList())).thenReturn(0, 1, 1);

        PowerMockito.when(IOUtils.readAndParseChoicesFromUser(Mockito.anyList()))
                // 0,1,2,3: choosen groups;
                .thenReturn(Arrays.asList(0, 1, 2, 3))
                // 0: remove uploaded material; 1: remove users action
                .thenReturn(Arrays.asList(0, 1));

        PowerMockito.when(IOUtils.readAndParseUserConfirmation()).thenReturn(true);
        PowerMockito.doNothing().when(endpointMock, "removeAllMembersFromGroups", groups);
        PowerMockito.when(endpointMock.getFilesFromGroups(groups)).thenReturn(files);
        PowerMockito.doNothing().when(endpointMock, "deleteObjectNodes", files);

        // == go
        createToolStateMachine().start();

        // == verify
        Mockito.verify(endpointMock, Mockito.times(1)).getCoursesForUser(DisplayStatus.ADMIN);

        PowerMockito.verifyStatic(Mockito.times(3));
        IOUtils.readAndParseSingleChoiceFromUser(Mockito.anyList());

        PowerMockito.verifyStatic(Mockito.times(2));
        IOUtils.readAndParseChoicesFromUser(Mockito.anyList());

        Mockito.verify(endpointMock, Mockito.times(1)).removeAllMembersFromGroups(groups);
        Mockito.verify(endpointMock, Mockito.times(1)).getFilesFromGroups(groups);
        Mockito.verify(endpointMock, Mockito.times(1)).deleteObjectNodes(files);
    }

    ////////////////
    // HELPERS
    ////////////////
    private void createEndpoint() throws Exception {
        loginConfiguration = LoginConfiguration.asLDAPLogin("myEndpoint", "client_id", "user", "pwd", 5);
        userDateIds = new UserDataIds(123, "sid", loginConfiguration.getUsername());
        PowerMockito.mockStatic(IOUtils.class);
        endpointMock = PowerMockito.mock(SoapEndpoint.class);
        PowerMockito.mockStatic(EndpointBuilder.class);
        PowerMockito.when(EndpointBuilder.build(Mockito.any(EndpointBuilder.Type.class), Mockito.eq(loginConfiguration)))
                .thenReturn(endpointMock);

//        toolStateMachineMock = PowerMockito.spy(new ToolStateMachine(loginConfiguration));
//        PowerMockito.when(toolStateMachineMock.getUserDataIds()).thenReturn(userDateIds);
    }

    private ToolStateMachine createToolStateMachine() {
//        return toolStateMachineMock;
        return new ToolStateMachine(loginConfiguration);
    }

}
