package com.github.mavogel.ilias.model;

import com.github.mavogel.ilias.state.ChangeAction;

import java.util.Collections;
import java.util.List;

/**
 * Created by mavogel on 9/11/16.
 */
public class IliasAction {

    private List<IliasNode> nodes;
    private List<ChangeAction> actions;

    public IliasAction(final List<IliasNode> nodes, final List<ChangeAction> actions) {
        if (nodes == null) {
             this.nodes = Collections.emptyList();
        } else {
             this.nodes = nodes;
        }
        if (actions == null) {
            this.actions = Collections.emptyList();
        } else {
            this.actions = actions;
        }
    }

    public IliasAction() {
        this(null, null);
    }

    public List<IliasNode> getNodes() {
        return nodes;
    }

    public List<ChangeAction> getActions() {
        return actions;
    }
}
