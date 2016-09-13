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

    private static Logger LOG = Logger.getLogger(ToolState.class);

    public StartState(final ToolStateMachine stateMachine, ToolState... successors) {
        super(stateMachine);
        setSuccessors(successors);
    }

    @Override
    public void printInformation() {
          LOG.info(" _______ __                        \n");
          LOG.info("|       |  |--.-----.              \n");
          LOG.info("|.|   | |     |  -__|              \n");
          LOG.info("`-|.  |-|__|__|_____|              \n");
          LOG.info("  |:  |                            \n");
          LOG.info("  |::.|                            \n");
          LOG.info("  `---'                            \n");
          LOG.info(" ___ ___     ___ _______ _______   \n");
          LOG.info("|   |   |   |   |   _   |   _   |  \n");
          LOG.info("|.  |.  |   |.  |.  1   |   1___|  \n");
          LOG.info("|.  |.  |___|.  |.  _   |____   |  \n");
          LOG.info("|:  |:  1   |:  |:  |   |:  1   |  \n");
          LOG.info("|::.|::.. . |::.|::.|:. |::.. . |  \n");
          LOG.info("`---`-------`---`--- ---`-------'  \n");
          LOG.info(" _______ __ __             __      \n");
          LOG.info("|   _   |  |__.-----.-----|  |_    \n");
          LOG.info("|.  1___|  |  |  -__|     |   _|   \n");
          LOG.info("|.  |___|__|__|_____|__|__|____|   \n");
          LOG.info("|:  1   |                          \n");
          LOG.info("|::.. . |                          \n");
          LOG.info("`-------'                          \n");
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
