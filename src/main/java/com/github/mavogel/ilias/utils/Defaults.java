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

import org.apache.log4j.Level;

import java.time.format.DateTimeFormatter;

/**
 * Default constants.
 *
 * Created by mavogel on 9/12/16.
 */
public final class Defaults {

    /**
     * The maximum folder depth to climb down the node tree starting from the root node and
     * look for specific type of nodes, e.g. groups
     */
    public static final int MAX_FOLDER_DEPTH = 5;

    /**
     * The default log level for the application.
     */
    public static final Level LOG_LEVEL = Level.INFO;

    /**
     * The wild card for the user input for making a choice.
     */
    public static final String CHOICE_WILDCARD = "A";

    /**
     * The format of the date in a user input
     */
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Denotes that all roles should be attached by default.
     */
    public static final int ATTACH_ROLES = 1;

    /**
     * Denotes that only active users should be considered.
     */
    public static final int IS_ACTIVE = 1;

    /**
     * The prefix for printing choices
     *
     * @param index the index
     * @return the prefix string
     */
    public static String GET_CHOICE_PREFIX(int index) {
        return "## --> [" + index + "] ";
    }

    /**
     * @return the visual surrounding of the choices
     */
    public static String GET_CHOICE_AROUND() {
        return "#################################################";
    }
}
