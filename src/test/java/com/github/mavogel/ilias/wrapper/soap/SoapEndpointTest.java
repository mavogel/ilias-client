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
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.wrapper.AbstractIliasEndpoint;
import com.github.mavogel.ilias.wrapper.DisplayStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
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
    public static final int USER_ID = 5678;
    private SoapEndpoint classUnderTest;
    private ILIASSoapWebservicePortType endPointMock;

    @Before
    public void setUp() throws Exception {
        // == prepare
        final LoginConfiguration loginConfiguration = LoginConfiguration.asLDAPLogin("myEndpoint", "client_id", "user", "pwd", 5);

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
        PowerMockito.when(endPointMock.getCoursesForUser(Mockito.eq(SID), Mockito.eq("courseXML")))
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

}
