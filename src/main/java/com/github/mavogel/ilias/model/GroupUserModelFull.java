package com.github.mavogel.ilias.model;/*
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

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * * A model for a group and its full users information.
 *
 * Created by mavogel on 9/20/16.
 */
public class GroupUserModelFull {

    private IliasNode groupNode;
    private List<IliasUser> groupMembers;

    public GroupUserModelFull(final IliasNode groupNode) {
        Validate.notNull(groupNode, "group node is null");
        this.groupNode = groupNode;
        this.groupMembers = new ArrayList<>();
    }

    public GroupUserModelFull(final IliasNode groupNode, final List<IliasUser> groupMembers) {
        Validate.notNull(groupNode, "group node is null");
        Validate.notNull(groupMembers, "group members are null");
        this.groupNode = groupNode;
        this.groupMembers = groupMembers;
    }

    public void addGroupMember(final IliasUser groupMemberToAdd) {
        this.groupMembers.add(groupMemberToAdd);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Group '");
        sb.append(groupNode.getTitle());
        if (!groupMembers.isEmpty()) {
            sb.append("' -> ").append(groupMembers.stream()
                    .map(gm -> gm.getLastName() + "," + gm.getFirstName() + " - " + gm.getMatriculationNumber())
                    .collect(Collectors.joining(" | ")));
        }
        return sb.toString();
    }
}
