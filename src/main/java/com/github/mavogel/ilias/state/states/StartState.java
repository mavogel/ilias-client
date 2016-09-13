package com.github.mavogel.ilias.state.states;

import com.github.mavogel.ilias.model.IliasAction;
import com.github.mavogel.ilias.model.IliasNode;
import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mavogel on 9/6/16.
 */
public class StartState extends ToolState {

    private static Logger LOG = Logger.getLogger(StartState.class);

    public StartState(final ToolStateMachine stateMachine, ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    public void printInformation() {
          LOG.info(" _______ __                       ");
          LOG.info("|       |  |--.-----.             ");
          LOG.info("|.|   | |     |  -__|             ");
          LOG.info("`-|.  |-|__|__|_____|             ");
          LOG.info("  |:  |                           ");
          LOG.info("  |::.|                           ");
          LOG.info("  `---'                           ");
          LOG.info(" ___ ___     ___ _______ _______  ");
          LOG.info("|   |   |   |   |   _   |   _   | ");
          LOG.info("|.  |.  |   |.  |.  1   |   1___| ");
          LOG.info("|.  |.  |___|.  |.  _   |____   | ");
          LOG.info("|:  |:  1   |:  |:  |   |:  1   | ");
          LOG.info("|::.|::.. . |::.|::.|:. |::.. . | ");
          LOG.info("`---`-------`---`--- ---`-------' ");
          LOG.info(" _______ __ __             __     ");
          LOG.info("|   _   |  |__.-----.-----|  |_   ");
          LOG.info("|.  1___|  |  |  -__|     |   _|  ");
          LOG.info("|.  |___|__|__|_____|__|__|____|  ");
          LOG.info("|:  1   |                         ");
          LOG.info("|::.. . |                         ");
          LOG.info("`-------'                         ");
    }

    @Override
    protected int printAndParseTransitionChoices() {
        return 0;
    }

    @Override
    protected List<IliasNode> collectDataForExecution() {
        return Collections.emptyList();
    }

    @Override
    protected IliasAction printAndParseExecutionChoices(final List<IliasNode> nodeChoices) {
        return new IliasAction();
    }

    @Override
    protected String doExecute(final IliasAction nodesAndActions) {
        return "";
    }
}
