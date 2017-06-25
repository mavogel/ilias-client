package com.github.mavogel.ilias.wrapper;/*
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

import com.github.mavogel.ilias.model.GroupUserModelFull;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.model.UserDataIds;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by mavogel on 6/25/17.
 * <p>
 * General wrapper for the Ilias endpoint. Atm only SOAP but maybe REST in the future.
 */
public interface IliasEndpoint {

    /**
     * Retrieves the user data from the Ilias backend.
     *
     * @param loginConfiguration the login data
     * @return the data ids of the user
     */
    UserDataIds getUserData(final LoginConfiguration loginConfiguration);

    /**
     * Logs out the user.
     */
    void logout();

    /**
     * Retrieves the courses for the user he has the given status in.<br>
     *
     * @param status the status
     * @return the courses
     * @see DisplayStatus for more details.
     */
    List<IliasNode> getCoursesForUser(final DisplayStatus... status);

    /**
     * Retrieves the groups of a given course by serching until a maximum folder depth.
     *
     * @param course         the course
     * @param maxFolderDepth the maximum folder depth
     * @return the groups
     */
    List<IliasNode> getGroupRefIdsFromCourses(final IliasNode course, final int maxFolderDepth);

    /**
     * Grants file upload permission to the users of a group.
     *
     * @param groups the groups
     */
    void grantFileUploadPermissionForMembers(final List<IliasNode> groups);

    /**
     * Retrieves the users for groups.
     *
     * @param groups the groups
     * @return the users
     */
    List<GroupUserModelFull> getUsersForGroups(final List<IliasNode> groups);

    /**
     * Retrieves the file nodes from the given groups.
     *
     * @param groups the groups
     * @return the file nodes
     */
    List<IliasNode> getFilesFromGroups(final List<IliasNode> groups);

    /**
     * Deletes the given files.
     *
     * @param files the files to delete
     */
    void deleteObjectNodes(final List<IliasNode> files);

    /**
     * Removes all members from the given groups.
     *
     * @param groups the groups
     */
    void removeAllMembersFromGroups(final List<IliasNode> groups);

    /**
     * Set a maximum amount of members for the given groups.
     *
     * @param groups the groups.
     */
    void setMaxMembersOnGroups(final List<IliasNode> groups);

    /**
     * Sets the registration start and end date for the given groups.<br>
     * Activates the registration for a group if it's not yet activated.
     *
     * @param groups the groups
     * @param start the start date
     * @param end the end data of the registration.
     */
    void setRegistrationDatesOnGroups(final List<IliasNode> groups, final LocalDateTime start, final LocalDateTime end);
}
