package com.github.mavogel.ilias.state.states;

import com.github.mavogel.ilias.state.ToolState;
import com.github.mavogel.ilias.state.ToolStateMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by mavogel on 9/6/16.
 */
public class ChooseTopicsState implements ToolState {

    private ToolStateMachine stateMachine;
    private List<ToolState> topics;

    public ChooseTopicsState(final ToolStateMachine stateMachine) {
        this.stateMachine = stateMachine;
        this.topics = new ArrayList<>();
        this.topics.add(null); // TODO
    }

    @Override
    public void printInformation() {
        System.out.println("Choose a topic");
    }

    @Override
    public void printExecutionChoices() {
        String exCoices = IntStream.range(0, topics.size())
                .mapToObj(i -> topics.get(i).asDisplayString(i + 1 + ") "))
                .collect(Collectors.joining("\n"));
        System.out.println(exCoices);
    }

    @Override
    public void printExecutionPreview() {

    }

    @Override
    public boolean confirm() {
        return false;
    }

    @Override
    public void execute() {

    }

    @Override
    public void printExectionSummary() {

    }

    @Override
    public void parseExecutionChoices() {

    }

    @Override
    public void printTransitionChoices() {

    }

    @Override
    public void parseTransitionChoice() {

    }

    @Override
    public void transition() {

    }

    @Override
    public String asDisplayString(final String prefix) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null && !prefix.isEmpty()) sb.append(prefix);
        // TODO
        return sb.toString();
    }
}
