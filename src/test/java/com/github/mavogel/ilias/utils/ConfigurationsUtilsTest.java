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
    public void shouldReadStdConfiguration() throws Exception {
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
