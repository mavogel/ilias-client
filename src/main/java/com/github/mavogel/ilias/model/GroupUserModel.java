package com.github.mavogel.ilias.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mavogel on 9/9/16.
 */
public class GroupUserModel {

    private IliasNode groupNode;
    private List<Integer> groupMemberIds;

    public GroupUserModel(final IliasNode groupNode) {
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
        sb.append(groupNode.getTitle()).append("' -> ");
        sb.append(groupMemberIds.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        return sb.toString();
    }
}
