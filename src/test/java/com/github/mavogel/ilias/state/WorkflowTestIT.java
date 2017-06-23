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

import com.github.mavogel.client.ILIASSoapWebserviceBindingStub;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.utils.IOUtils;
import com.github.mavogel.ilias.utils.IliasUtils;
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

/**
 * Created by mavogel on 6/21/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ToolStateMachine.class, WorkflowTestIT.class, IliasUtils.class, IOUtils.class})
public class WorkflowTestIT {

    private LoginConfiguration loginConfiguration;
    private ILIASSoapWebserviceBindingStub endpointMock;
    private UserDataIds userDateIds;

    @Before
    public void setUp() throws Exception {
        createEndpoint();
    }

    @Test
    public void shouldFindNoCoursesAndQuitThen() throws Exception {
        // == prepare
        final List<IliasNode> noCourses = Collections.emptyList();

        // == train
        PowerMockito.when(IliasUtils.getCoursesForUser(endpointMock, userDateIds.getSid(),
                userDateIds.getUserId(), IliasUtils.DisplayStatus.ADMIN))
                .thenReturn(noCourses);
        PowerMockito.when(IOUtils.readAndParseSingleChoiceFromUser(Mockito.anyList())).thenReturn(0, 2);

        // == go
        createToolStateMachine().start();

        // == verify
        PowerMockito.verifyStatic(Mockito.times(2));
        IliasUtils.getCoursesForUser(endpointMock, userDateIds.getSid(),
                userDateIds.getUserId(), IliasUtils.DisplayStatus.ADMIN);
        PowerMockito.verifyStatic(Mockito.times(2));
        IOUtils.readAndParseSingleChoiceFromUser(Mockito.anyList());
    }

    @Test
    public void shouldOneNCoursesAndQuitThen() throws Exception {
        // == prepare
        final List<IliasNode> courses = Arrays.asList(new IliasNode(1, IliasNode.Type.COURSE, "My Course"));

        // == train
        PowerMockito.when(IliasUtils.getCoursesForUser(endpointMock, userDateIds.getSid(),
                userDateIds.getUserId(), IliasUtils.DisplayStatus.ADMIN))
                .thenReturn(courses);
        PowerMockito.when(IOUtils.readAndParseSingleChoiceFromUser(Mockito.anyList())).thenReturn(0, 2);

        // == go
        createToolStateMachine().start();

        // == verify
        PowerMockito.verifyStatic(Mockito.times(1));
        IliasUtils.getCoursesForUser(endpointMock, userDateIds.getSid(),
                userDateIds.getUserId(), IliasUtils.DisplayStatus.ADMIN);
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
        PowerMockito.when(IliasUtils.getCoursesForUser(endpointMock, userDateIds.getSid(),
                userDateIds.getUserId(), IliasUtils.DisplayStatus.ADMIN))
                .thenReturn(courses);
        PowerMockito.when(IliasUtils.retrieveGroupRefIdsFromCourses(endpointMock, userDateIds.getSid(),
                userDateIds.getUserId(), courses, 5)).thenReturn(groups);
        // 0: choose course; 1: actionsOnGroups; 1 quit
        PowerMockito.when(IOUtils.readAndParseSingleChoiceFromUser(Mockito.anyList())).thenReturn(0, 1, 1);

        PowerMockito.when(IOUtils.readAndParseChoicesFromUser(Mockito.anyList()))
                // 0,1,2: choosen groups;
                .thenReturn(Arrays.asList(0, 1, 2))
                // 1: remove users action
                .thenReturn(Arrays.asList(1));

        PowerMockito.when(IOUtils.readAndParseUserConfirmation()).thenReturn(true);
        PowerMockito.doNothing().when(IliasUtils.class, "removeAllMembersFromGroups", endpointMock, userDateIds.getSid(), groups);

        // == go
        createToolStateMachine().start();

        // == verify
        PowerMockito.verifyStatic(Mockito.times(1));
        IliasUtils.getCoursesForUser(endpointMock, userDateIds.getSid(),
                userDateIds.getUserId(), IliasUtils.DisplayStatus.ADMIN);
        PowerMockito.verifyStatic(Mockito.times(3));
        IOUtils.readAndParseSingleChoiceFromUser(Mockito.anyList());
        PowerMockito.verifyStatic(Mockito.times(2));
        IOUtils.readAndParseChoicesFromUser(Mockito.anyList());
        PowerMockito.verifyStatic(Mockito.times(1));
        IliasUtils.removeAllMembersFromGroups(endpointMock, userDateIds.getSid(), groups);
    }

    ////////////////
    // HELPERS
    ////////////////
    private void createEndpoint() throws javax.xml.rpc.ServiceException, RemoteException {
        loginConfiguration = LoginConfiguration.asLDAPLogin("ep", "client", "user", "pwd", 5);
        userDateIds = new UserDataIds(123, "sid", loginConfiguration.getUsername());
        endpointMock = PowerMockito.mock(ILIASSoapWebserviceBindingStub.class);
        PowerMockito.mockStatic(IliasUtils.class);
        PowerMockito.mockStatic(IOUtils.class);
        PowerMockito.when(IliasUtils.createWsEndpoint(loginConfiguration)).thenReturn(endpointMock);
        PowerMockito.when(IliasUtils.getUserData(loginConfiguration, endpointMock)).thenReturn(userDateIds);
    }

    private ToolStateMachine createToolStateMachine() {
        return new ToolStateMachine(loginConfiguration);
    }

}
