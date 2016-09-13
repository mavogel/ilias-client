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
package com.github.mavogel.ilias.state.states;

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.state.ChangeAction;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.state.states.action.RemoveUploadedMaterialsChange;
import com.github.mavogel.ilias.utils.IOUtils;
import com.github.mavogel.ilias.utils.IliasUtils;
import com.github.mavogel.ilias.utils.XMLUtils;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mavogel on 9/7/16.
 */
public class ChooseCoursesState extends ToolState {

    private static Logger LOG = Logger.getLogger(ChooseCoursesState.class);

    public ChooseCoursesState(final ToolStateMachine stateMachine, final ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    public void printInformation() {
        LOG.info("Choose a Course:");
        this.stateMachine.getContext().remove(ToolStateMachine.ContextKey.GROUPS);
    }

    @Override
    protected List<IliasNode> collectDataForExecution() {
        final ILIASSoapWebservicePortType endpoint = stateMachine.getEndPoint();
        final String sid = stateMachine.getUserDataIds().getSid();
        final int userId = stateMachine.getUserDataIds().getUserId();

        try {
            return IliasUtils.getCoursesForUser(endpoint, sid, userId, IliasUtils.DisplayStatus.ADMIN);
        } catch (RemoteException e) {
            LOG.error("Could not retrieve courses for user : " + e.getMessage());
            this.stateMachine.setState(stateMachine.getQuitState());
        } catch (JDOMException | IOException e) {
            LOG.error("Error creating xml parser: " + e.getMessage());
            this.stateMachine.setState(stateMachine.getQuitState());
        }
        return Collections.emptyList();
    }

    @Override
    protected IliasAction printAndParseExecutionChoices(final List<IliasNode> nodeChoices) {
        IntStream.range(0, nodeChoices.size())
                .mapToObj(i -> nodeChoices.get(i).asDisplayString(i + ") "))
                .forEach(System.out::println);

        List<Integer> indexesOfChosenNodes = IOUtils.readAndParseChoicesFromUser(nodeChoices);
        List<IliasNode> choseIliasNodes = indexesOfChosenNodes.stream()
                .map(idx -> nodeChoices.get(idx))
                .collect(Collectors.toList());

        return new IliasAction(choseIliasNodes, null);
    }

    @Override
    protected String doExecute(final IliasAction nodesAndActions) {
        stateMachine.getContext().put(ToolStateMachine.ContextKey.COURSES, nodesAndActions.getNodes());
        return "";
    }
}
