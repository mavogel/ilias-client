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

import com.github.mavogel.ilias.lib.model.IliasNode;
import com.github.mavogel.ilias.state.ChangeAction;

import java.util.Collections;
import java.util.List;

/**
 * Represents the models for a set of given action which shall be applied to each node.
 *
 * Created by mavogel on 9/11/16.
 */
public class IliasAction {

    private List<IliasNode> nodes;
    private List<ChangeAction> actions;

    /**
     * Attaches one or more actions on one or more nodes.<br>
     * If the parameters are <code>null</code> they are initialized empty.
     *
     * @param nodes the nodes
     * @param actions the actions
     */
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

    /**
     * Creates an empty ilias action model
     */
    public IliasAction() {
        this(null, null);
    }

    public List<IliasNode> getNodes() {
        return nodes;
    }

    public List<ChangeAction> getActions() {
        return actions;
    }

    /**
     * @return true if the IliasAction has neither nodes nor actios, false otherwise
     */
    public boolean isCompletelyEmpty() {
        return this.nodes.isEmpty() && this.actions.isEmpty();
    }
}
