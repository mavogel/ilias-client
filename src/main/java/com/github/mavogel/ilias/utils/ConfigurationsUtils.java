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
        String client, username, password;

        try {
            Configuration config = builder.getConfiguration();
            loginMode = LoginConfiguration.LOGIN_MODE.valueOf(config.getString("login.mode"));
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
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        switch (loginMode) {
            case STD:
                return LoginConfiguration.asStandardLogin(client, username, password);
            case LDAP:
                return LoginConfiguration.asLDAPLogin(client, username, password);
            case CAS:
                return LoginConfiguration.asCASLogin();
            default:
                throw new UnsupportedOperationException(String.format("LOGIN_MODE: '%s' not yet supported", loginMode));
        }
    }
}
