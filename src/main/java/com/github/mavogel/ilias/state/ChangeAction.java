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
package com.github.mavogel.ilias.state;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.UserDataIds;

import java.util.List;

/**
 * Interface to actions on nodes.
 *
 * Created by mavogel on 9/7/16.
 */
public interface ChangeAction {

    /**
     * Performs the action of the state.
     *
     * @param endpoint    the endpoint of the Webservice
     * @param userDataIds the ids of the users
     * @param nodes       the nodes to perform the action on
     * @return the summary of the performed action
     */
    String performAction(final ILIASSoapWebservicePortType endpoint, final UserDataIds userDataIds,
                         final List<IliasNode> nodes);

    /**
     * Prints and requests a confirmation from the user for the upcoming action.<br>
     * Does <b>NOT</b> change the context.
     *
     * @return <code>true</code> for as yes input, <code>false</code> otherwise
     */
    boolean confirm();

    /**
     * The name of the action
     *
     * @param prefix an optional header
     * @return its name
     */
    default String actionName(final String prefix) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null && !prefix.isEmpty()) sb.append(prefix);
        sb.append(this.getClass().getSimpleName().replace("Action", ""));
        return sb.toString();
    }
}
