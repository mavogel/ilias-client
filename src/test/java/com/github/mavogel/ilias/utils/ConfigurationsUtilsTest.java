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

import com.github.mavogel.ilias.model.LoginConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.Console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mavogel on 8/30/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigurationsUtils.class, ConfigurationsUtilsTest.class})
public class ConfigurationsUtilsTest {

    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String BASE_DIR = System.getProperty("user.dir");
    private static final String TEST_RES_DIR = BASE_DIR + FILE_SEP + "src" + FILE_SEP + "test" + FILE_SEP + "resources" + FILE_SEP;

    @Test
    public void shouldReadValidConfiguration() throws Exception {
        // == prepare
        String testFile = TEST_RES_DIR + "testConfig.properties";

        // == go
        LoginConfiguration validLoginConfiguration = ConfigurationsUtils.createLoginConfiguration(testFile);

        // == verify
        assertEquals(LoginConfiguration.LOGIN_MODE.LDAP, validLoginConfiguration.getLoginMode());
        assertEquals("https://mycompany.com/webservice/soap/server.php", validLoginConfiguration.getEndpoint());
        assertEquals("MY_CLIENT", validLoginConfiguration.getClient());
        assertEquals("user", validLoginConfiguration.getUsername());
        assertEquals("pass", validLoginConfiguration.getPassword());
        assertEquals(3, validLoginConfiguration.getMaxFolderDepth());
    }

    @Test
    public void shouldPromptForPassword() throws Exception {
        // == prepare
        String testFile = TEST_RES_DIR + "testConfigWoutPwd.properties";

        // == train
        PowerMockito.mockStatic(System.class);
        final Console consoleMock = PowerMockito.mock(Console.class);
        Mockito.when(System.console()).thenReturn(consoleMock);
        Mockito.when(consoleMock.readPassword("Enter your password: ")).thenReturn("mypass123".toCharArray());


        // == go
        LoginConfiguration validLoginConfiguration = ConfigurationsUtils.createLoginConfiguration(testFile);

        // == verify
        assertEquals(LoginConfiguration.LOGIN_MODE.LDAP, validLoginConfiguration.getLoginMode());
        assertEquals("https://mycompany.com/webservice/soap/server.php", validLoginConfiguration.getEndpoint());
        assertEquals("MY_CLIENT", validLoginConfiguration.getClient());
        assertEquals("user", validLoginConfiguration.getUsername());
        assertEquals("mypass123", validLoginConfiguration.getPassword());
        assertEquals(3, validLoginConfiguration.getMaxFolderDepth());
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailBecauseConsoleIsNotAvailable() throws Exception {
        // == prepare
        String testFile = TEST_RES_DIR + "testConfigWoutPwd.properties";

        // == train
        PowerMockito.mockStatic(System.class);
        Mockito.when(System.console()).thenReturn(null);

        // == go
        try {
            ConfigurationsUtils.createLoginConfiguration(testFile);
        } catch (Exception e) {
            // == verify
            throw e;
        }
    }

    @Test
    public void shouldReadStdWithoutFolderDepthConfiguration() throws Exception {
        // == prepare
        String testFile = TEST_RES_DIR + "testConfigStdLogin.properties";

        // == go
        LoginConfiguration validLoginConfiguration = ConfigurationsUtils.createLoginConfiguration(testFile);

        // == verify
        assertEquals(LoginConfiguration.LOGIN_MODE.STD, validLoginConfiguration.getLoginMode());
        assertEquals("https://mycompany.com/webservice/soap/server.php", validLoginConfiguration.getEndpoint());
        assertEquals("MY_CLIENT", validLoginConfiguration.getClient());
        assertEquals("user", validLoginConfiguration.getUsername());
        assertEquals("pass", validLoginConfiguration.getPassword());
        assertEquals(5, validLoginConfiguration.getMaxFolderDepth());
    }

    @Test
    public void shouldReadStdWithEmptyFolderDepthConfiguration() throws Exception {
        // == prepare
        String testFile = TEST_RES_DIR + "testConfigFolderDepthEmpty.properties";

        // == go
        LoginConfiguration validLoginConfiguration = ConfigurationsUtils.createLoginConfiguration(testFile);

        // == verify
        assertEquals(LoginConfiguration.LOGIN_MODE.STD, validLoginConfiguration.getLoginMode());
        assertEquals("https://mycompany.com/webservice/soap/server.php", validLoginConfiguration.getEndpoint());
        assertEquals("MY_CLIENT", validLoginConfiguration.getClient());
        assertEquals("user", validLoginConfiguration.getUsername());
        assertEquals("pass", validLoginConfiguration.getPassword());
        assertEquals(5, validLoginConfiguration.getMaxFolderDepth());
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailDueToUnknownMode() throws Exception {
        // == prepare
        String testFile = TEST_RES_DIR + "testConfigUnknownMode.properties";

        try {
            // == go
            ConfigurationsUtils.createLoginConfiguration(testFile);
        } catch (Exception e) {
            // verify
            assertTrue(e.getMessage().contains("Login mode"));
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailEmptyClientString() throws Exception {
        // == prepare
        String testFile = TEST_RES_DIR + "bogusTestConfig.properties";

        try {
            // == go
            ConfigurationsUtils.createLoginConfiguration(testFile);
        } catch (Exception e) {
            // verify
            assertTrue(e.getMessage().contains("empty"));
            throw e;
        }
    }
}
