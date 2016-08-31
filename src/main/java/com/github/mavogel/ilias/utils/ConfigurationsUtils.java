package com.github.mavogel.ilias.utils;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;

import java.io.Console;

/**
 * Created by mavogel on 8/30/16.
 * <p>
 * Utils for the configuration.
 */
public class ConfigurationsUtils {

    /**
     * Creates the login confuiguration
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

        try {
            Configuration config = builder.getConfiguration();
            loginModeRaw = config.getString("login.mode");
            loginMode = LoginConfiguration.LOGIN_MODE.valueOf(loginModeRaw);
            endpoint = config.getString("endpoint");
            client = config.getString("login.client");
            username = config.getString("login.username");
            password = config.getString("login.password");
            if (password == null || password.isEmpty()) {
                Console console = System.console();
                if (console != null) {
                    password = String.valueOf(console.readPassword("Enter your password: "));
                } else {
                    throw new RuntimeException("Console is not available!");
                }
            }
        } catch (IllegalArgumentException iae) {
            throw new RuntimeException("Login mode '" + loginModeRaw + "' is unknown");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        switch (loginMode) {
            case STD:
                return LoginConfiguration.asStandardLogin(endpoint, client, username, password);
            case LDAP:
                return LoginConfiguration.asLDAPLogin(endpoint, client, username, password);
            case CAS:
                return LoginConfiguration.asCASLogin();
            default:
                // should not happen
                return null;
        }
    }
}
