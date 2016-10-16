package com.github.mavogel.ilias.state.states.action;

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

import com.github.mavogel.client.ILIASSoapWebservicePortType;
import com.github.mavogel.ilias.model.GroupUserModelFull;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.model.UserDataIds;
import com.github.mavogel.ilias.printer.VelocityOutputPrinter;
import com.github.mavogel.ilias.state.ChangeAction;
import com.github.mavogel.ilias.state.ToolStateMachine;
import com.github.mavogel.ilias.utils.Defaults;
import com.github.mavogel.ilias.utils.IOUtils;
import com.github.mavogel.ilias.utils.IliasUtils;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents the action for retrieving all groups with its users and stores the in
 * a Latex file. It's kept simple atm.
 * <p>
 * Created by mavogel on 9/20/16.
 */
public class PrintGroupMembersAction implements ChangeAction {

    private static Logger LOG = Logger.getLogger(PrintGroupMembersAction.class);

    public enum ContextKeys {
        TITLE("title"),
        MEMBERS_PER_GROUP("membersPerGroup"),
        COLUMS_ORDER("columnsOrder"),
        COLUMS_COUNT("columnsCount");

        private final String velocityKey;

        ContextKeys(final String velocityKey) {
            this.velocityKey = velocityKey;
        }

        public String getVelocityKey() {
            return velocityKey;
        }
    }

    @Override
    public void performAction(final ILIASSoapWebservicePortType endpoint, Map<ToolStateMachine.ContextKey, List<IliasNode>> context,
                              final UserDataIds userDataIds, final List<IliasNode> nodes) {
        LOG.info("Print group members");
        if (confirm()) {
            final String sid = userDataIds.getSid();
            try {
                List<GroupUserModelFull> membersPerGroup = IliasUtils.getUsersForGroups(endpoint, sid, nodes);
                IntStream.range(0, VelocityOutputPrinter.OutputType.values().length)
                        .mapToObj(i -> VelocityOutputPrinter.OutputType.getAtIndex(i).asDisplayString(Defaults.GET_CHOICE_PREFIX(i)))
                        .forEach(LOG::info);
                List<Integer> outputChoicesIdx = IOUtils.readAndParseChoicesFromUser(Arrays.stream(VelocityOutputPrinter.OutputType.values()).collect(Collectors.toList()));

                HashMap<String, Object> contextMap = new HashMap<>();
                for (Integer idxChoice : outputChoicesIdx) {
                    VelocityOutputPrinter.OutputType outputType = VelocityOutputPrinter.OutputType.getAtIndex(idxChoice);
                    LOG.info("Path to template for '" + outputType + "':");
                    String templatePath = IOUtils.readLine();

                    switch (outputType) {
                        case LATEX:
                            contextMap.put(ContextKeys.COLUMS_ORDER.getVelocityKey(), "| c | p{2.5cm} | p{2.5cm} | p{2.5cm} | p{2.5cm} | p{2.5cm} |");
                        case HTML:
                            contextMap.put(ContextKeys.TITLE.getVelocityKey(), context.get(ToolStateMachine.ContextKey.COURSES).get(0).getTitle()); // TODO atm we only have one course in the context
                            contextMap.put(ContextKeys.MEMBERS_PER_GROUP.getVelocityKey(), membersPerGroup);
                            contextMap.put(ContextKeys.COLUMS_COUNT.getVelocityKey(), Arrays.asList("1", "2", "3", "4", "5"));
                            break;
                        default:
                            throw new RuntimeException("output type '" + outputType + "' not yet implemented for filling context map!");
                    }

                    boolean isTemplateWritten = false;
                    while (!isTemplateWritten) {
                        try {
                            VelocityOutputPrinter.print(outputType, templatePath, contextMap);
                            isTemplateWritten = true;
                            LOG.info(outputType + " output successfully written!");
                        } catch (Exception e) {
                            outputType = VelocityOutputPrinter.OutputType.getAtIndex(idxChoice);
                            LOG.info("Path to template for '" + outputType + "':");
                            templatePath = IOUtils.readLine();
                        }
                    }

                    contextMap.clear();
                }
            } catch (IOException | JDOMException e) {
                LOG.error("Error creating xml parser: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean confirm() {
        return true;
    }
}
