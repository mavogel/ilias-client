/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016 Manuel Vogel
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies
 *  or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 *  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  https://opensource.org/licenses/MIT
 */
package com.github.mavogel.ilias.model;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A model for a group and its users.
 *
 * Created by mavogel on 9/9/16.
 */
public class GroupUserModel {

    private IliasNode groupNode;
    private List<Integer> groupMemberIds;

    public GroupUserModel(final IliasNode groupNode) {
        Validate.notNull(groupNode, "Cannot create a group user model with a group");
        this.groupNode = groupNode;
        this.groupMemberIds = new ArrayList<>();
    }

    /**
     * Adds another memberId
     *
     * @param groupMemberId the id to add
     */
    public void addGroupMemberId(final Integer groupMemberId) {
        this.groupMemberIds.add(groupMemberId);
    }

    /**
     * @return <code>true</code> if there are group members, <code>false</code> otherwise.
     */
    public boolean hasMembers() {
        return !groupMemberIds.isEmpty();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Group '");
        sb.append(groupNode.getTitle());
        if (!groupMemberIds.isEmpty()) {
            sb.append("' -> ").append(groupMemberIds.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }
        return sb.toString();
    }
}
