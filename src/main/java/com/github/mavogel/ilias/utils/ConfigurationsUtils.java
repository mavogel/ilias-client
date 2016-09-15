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
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConversionException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.Console;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utils for parsing the config file and creating the login configuration.
 *
 * Created by mavogel on 8/30/16.
 */
public class ConfigurationsUtils {

    /**
     * Creates the login configuration and uses defaults if necessary.
     *
     * @param propertyFilename the name of the property file
     * @return the {@link LoginConfiguration}
     */
    public static LoginConfiguration createLoginConfiguration(final String propertyFilename) {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties().setFileName(propertyFilename));
        LoginConfiguration.LOGIN_MODE loginMode = null;
        String endpoint, client, username, password, loginModeRaw = "";
        int maxFolderDepth;
        Level logLevel;

        try {
            Configuration config = builder.getConfiguration();
            loginModeRaw = config.getString("login.mode");
            loginMode = LoginConfiguration.LOGIN_MODE.valueOf(loginModeRaw);
            endpoint = config.getString("endpoint");
            client = config.getString("login.client");
            username = config.getString("login.username");
            password = config.getString("login.password");
            maxFolderDepth = config.getInt("maxFolderDepth", Defaults.MAX_FOLDER_DEPTH);
            logLevel= Level.toLevel(config.getString("log.level", Defaults.LOG_LEVEL.toString()), Defaults.LOG_LEVEL);
            if (password == null || password.isEmpty()) {
                Console console = System.console();
                if (console != null) {
                    password = String.valueOf(console.readPassword("Enter your password: "));
                } else {
                    throw new Exception("Console is not available!");
                }
            }
        } catch (IllegalArgumentException iae) {
            throw new RuntimeException(String.format("Login mode '%s' is unknown. Use one of %s",
                    loginModeRaw, Arrays.stream(LoginConfiguration.LOGIN_MODE.values())
                                        .map(lm -> lm.name()).collect(Collectors.joining(", "))));
        } catch (ConversionException ce) {
            throw new RuntimeException("maxFolderDepth property is not an integer");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        // == 1: set log level
        Logger.getRootLogger().setLevel(logLevel);

        // == 2: create login configuration
        switch (loginMode) {
            case STD:
                return LoginConfiguration.asStandardLogin(endpoint, client, username, password, maxFolderDepth);
            case LDAP:
                return LoginConfiguration.asLDAPLogin(endpoint, client, username, password, maxFolderDepth);
            case CAS:
                return LoginConfiguration.asCASLogin();
            default:
                // should not happen
                return null;
        }
    }
}
