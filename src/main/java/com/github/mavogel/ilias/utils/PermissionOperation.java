package com.github.mavogel.ilias.utils;/*
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

import java.util.Arrays;

/**
 * The permission for a role in Ilias.
 * <p>
 * Can be used for {@link com.github.mavogel.client.ILIASSoapWebservicePortType#grantPermissions(String, int, int, int[])}
 *
 * Created by mavogel on 9/20/16.
 */
public enum PermissionOperation {

    EDIT_PERMISSION(1), // edit permissions
    VISIBLE(2), // view object
    READ(3), // access object
    WRITE(4), // modify object
    DELETE(6), // remove object
    JOIN(7), // join/subscribe
    LEAVE(8), // leave/unsubscribe
    ADD_REPLY(9), // Reply to forum articles
    MODERATE_FRM(10), // delete forum articles
    SMTP_MAIL(11), // send external mail
    SYSTEM_MESSAGE(12), // allow to send system messages
    CREATE_USR(13), // create new user account
    CREATE_ROLE(14), // create new role definition
    CREATE_ROLT(15), // create new role definition template
    CREATE_CAT(16), // create new category
    CREATE_GRP(17), // create new group
    CREATE_FRM(18), // create new forum
    CREATE_CRS(19), // create new course
    CREATE_LM(20), // create new learning module
    CREATE_SAHS(21), // create new SCORM/AICC learning module
    CREATE_GLO(22), // create new glossary
    CREATE_DBK(23), // create new digibook
    CREATE_EXC(24), // create new exercise
    CREATE_FILE(25), // upload new file
    CREATE_FOLD(26), // create new folder
    CREATE_TST(27), // create new test
    CREATE_QPL(28), // create new question pool
    INTERNAL_MAIL(30), // users can use mail system
    CREATE_MEP(31), // create new media pool
    CREATE_HTLM(32), // create new html learning module
    EDIT_USERASSIGNMENT(40), // change userassignment of roles
    EDIT_ROLEASSIGNMENT(41), // change roleassignments of user accounts
    CREATE_SVY(42), // create new survey
    CREATE_SPL(43), // create new question pool (Survey)
    INVITE(45), // invite
    CAT_ADMINISTRATE_USERS(47), // Administrate local user
    READ_USERS(48), // read local users
    PUSH_DESKTOP_ITEMS(49), // Allow pushing desktop items
    CREATE_WEBR(50), // create web resource
    SEARCH(51), // Allow using search
    MODERATE(52), // Moderate objects
    CREATE_ICRS(53), // create LearnLink Seminar
    CREATE_ICLA(54), // create LearnLink Seminar room
    EDIT_LEARNING_PROGRESS(55), // edit learning progress
    TST_STATISTICS(56), // view the statistics of a test
    EXPORT_MEMBER_DATA(57), // Export member data
    COPY(58), // Copy Object
    CREATE_FEED(59), // create external feed
    CREATE_MCST(60), // create media cast
    CREATE_RCRS(61), // create remote course
    ADD_THREAD(62), // Add Threads
    CREATE_SESS(63), // create session
    EDIT_CONTENT(64), // Edit content
    CREATE_WIKI(65), // create wiki
    EDIT_EVENT(66), // Edit calendar event
    CREATE_CRSR(67), // create course reference
    CREATE_CATR(68), // create category reference
    MAIL_TO_GLOBAL_ROLES(69), // User may send mails to global roles
    CREATE_BOOK(71), // create booking pool
    ADD_CONSULTATION_HOURS(72), // Add Consultation Hours Calendar
    CREATE_CHTR(73), // create chatroom
    CREATE_BLOG(74), // Create Blog
    CREATE_DCL(75), // Create Data Collection
    CREATE_POLL(76), // Create Poll
    ADD_ENTRY(77), // Add Entry
    CREATE_ITGR(78), // Create Item Group
    CONTRIBUTE(79), // Contribute
    LP_OTHER_USERS(80), // See LP Data Of Other Users
    CREATE_BIBL(81), // Create Bibliographic
    CREATE_CLD(82), // Create Cloud Folder
    UPLOAD(83), // Upload Items
    DELETE_FILES(84), // Delete Files
    DELETE_FOLDERS(85), // Delete Folders
    DOWNLOAD(86), // Download Items
    FILES_VISIBLE(87), // Files are visible
    FOLDERS_VISIBLE(88), // Folders are visible
    FOLDERS_CREATE(89), // Folders may be created
    CREATE_PRTT(90), // Create Portfolio Template
    CREATE_ORGU(91), // Create OrgUnit
    VIEW_LEARNING_PROGRESS(92), // View learning progress from users in this orgu.
    VIEW_LEARNING_PROGRESS_REC(93), // View learning progress from users in this orgu and subsequent orgus.
    STATISTICS_READ(94), // Read Statistics
    READ_LEARNING_PROGRESS(95),; // Read Learning Progress

    private final int opsId;

    PermissionOperation(final int opsId) {
        this.opsId = opsId;
    }

    /**
     * @return the id of the operation.
     */
    public int getOpsId() {
        return opsId;
    }

    /**
     * Builds the operations for the ilias endpoint.
     *
     * @param ops the operation to build.
     * @return the ids of the operations.
     */
    public static int[] build(PermissionOperation... ops) {
        if (ops == null) {
            return new int[]{};
        } else {
            return Arrays.stream(ops).filter(o -> o != null).mapToInt(o -> o.getOpsId()).toArray();
        }
    }
}
