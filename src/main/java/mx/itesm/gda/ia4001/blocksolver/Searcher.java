/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4001.blocksolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author alexv
 */
public class Searcher {

    private static final Log LOGGER = LogFactory.getLog(Searcher.class);

    private final State initialState;

    private final State finalState;

    public static enum SearchType {

        DEPTH_FIRST, BREATH_FIRST;

    }

    public Searcher(State initial_state, State final_state) {
        initialState = initial_state;
        finalState = final_state;
    }

    public List<State.Move> searchSolution(SearchType search_type) {

        Deque<StatePathPair> states_to_inspect =
                new LinkedList<StatePathPair>();

        Set<State> visited_nodes = new HashSet<State>();

        states_to_inspect.add(new StatePathPair(initialState,
                Collections.EMPTY_LIST));

        while(states_to_inspect.size() > 0) {
            StatePathPair entry = states_to_inspect.removeFirst();

            State current_state = entry.state;
            List<State.Move> current_path = entry.path;
            visited_nodes.add(current_state);

            if(finalState.equals(current_state)) {
                return current_path;
            }

            Set<State.Move> possible_moves = current_state.possibleMoves();

            for(State.Move potential_move : possible_moves) {
                State next_state = current_state.executeMove(potential_move);

                if(!visited_nodes.contains(next_state)) {
                    List<State.Move> next_path =
                            new ArrayList<State.Move>(current_path);

                    next_path.add(potential_move);
                    StatePathPair next_pair = new StatePathPair(next_state,
                            next_path);

                    switch(search_type) {
                        case DEPTH_FIRST:
                            states_to_inspect.addFirst(next_pair);
                            break;
                        case BREATH_FIRST:
                            states_to_inspect.addLast(next_pair);
                            break;
                    }
                }
            }
        }

        throw new RuntimeException("Unreachable state");
    }

    private static void logSolution(State initial_state,
            List<State.Move> solution) {
        LOGGER.info("Solution found");

        State current_state = initial_state;
        for(State.Move step : solution) {
            State next_state = current_state.executeMove(step);
            LOGGER.info(current_state + " -> " + next_state + ": " + step);
            current_state = next_state;
        }

    }

    public static void main(String[] args) {
        State initial_state = new State("SEXILA");
        State final_state = new State("ALEXIS");

        Searcher searcher = new Searcher(initial_state, final_state);

        LOGGER.info("Looking for solution DEPTH_FIRST for "
                + initial_state + " -> " + final_state);

        try {
            List<State.Move> solution = searcher.searchSolution(
                    SearchType.DEPTH_FIRST);
            logSolution(initial_state, solution);

        } catch(Exception e) {
            LOGGER.info("Solution not found: " + e.getMessage(), e);

        }

        LOGGER.info("Looking for solution BREATH_FIRST for "
                + initial_state + " -> " + final_state);

        try {
            List<State.Move> solution = searcher.searchSolution(
                    SearchType.BREATH_FIRST);
            logSolution(initial_state, solution);

        } catch(Exception e) {
            LOGGER.info("Solution not found: " + e.getMessage(), e);

        }
    }

    private static class StatePathPair {

        private final State state;

        private final List<State.Move> path;

        private StatePathPair(State my_state, List<State.Move> my_path) {
            state = my_state;
            path = Collections.unmodifiableList(
                    new ArrayList<State.Move>(my_path));
        }

    }

}
