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
package com.github.mavogel.ilias.state.states.action;

import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.state.ChangeAction;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.IOUtils;
import com.github.mavogel.ilias.wrapper.IliasEndpoint;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Represents the action for removing/excluding the users of a group.
 * <p>
 * Created by mavogel on 9/7/16.
 */
public class RemoveUsersAction implements ChangeAction {

    private static Logger LOG = Logger.getLogger(RemoveUsersAction.class);

    @Override
    public void performAction(final IliasEndpoint endpoint,
                              final Map<ToolStateMachine.ContextKey, List<IliasNode>> context,
                              final List<IliasNode> nodes) {
        LOG.info("Removing users from groups");
        if (confirm()) {
            try {
                endpoint.removeAllMembersFromGroups(nodes);
            } catch (Exception e) {
                LOG.error("Error Removing users from groups: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean confirm() {
        return IOUtils.readAndParseUserConfirmation();
    }
}
