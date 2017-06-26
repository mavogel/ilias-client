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
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.wrapper.AbstractIliasEndpoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by mavogel on 6/26/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SoapEndpoint.class, AbstractIliasEndpoint.class,
        ILIASSoapWebservicePortType.class, ILIASSoapWebserviceLocator.class})
public class SoapEndpointTest {

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
                .thenReturn("sid-123");
        PowerMockito.when(endPointMock.getUserIdBySid("sid-123")).thenReturn(5678);

        // == set
        classUnderTest = new SoapEndpoint(loginConfiguration);
    }

    @Test
    public void shouldLogout() throws Exception {
        // == go
        PowerMockito.when(endPointMock.logout("sid-123")).thenReturn(true);

        // == go
        classUnderTest.logout();

        // == verify
        Mockito.verify(endPointMock).logout("sid-123");
    }
}
