/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4001.blocksolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author alexv
 */
public class State {

    private static final Log LOGGER = LogFactory.getLog(State.class);

    public static final char EMPTY = '\ufffe';

    private final String state;

    public State(String... columns) {
        String[] sorted_columns = Arrays.copyOf(columns, columns.length);
        Arrays.sort(sorted_columns);

        StringBuilder sb = new StringBuilder();
        for(String column : sorted_columns) {
            if(column.length() > 0) {
                sb.append(column);
                sb.append('|');
            }
        }

        if(sb.length() > 0) {
            sb.setLength(sb.length() - 1);

        }

        state = sb.toString();

    }

    @Override
    public String toString() {
        return state;
    }

    public Set<Move> possibleMoves() {
        Set<Move> moves = new HashSet<Move>();

        List<String> columns = Arrays.asList(state.split("\\|"));

        for(int i = 0; i < columns.size(); i++) {
            String column_origin = columns.get(i);
            char origin_block = column_origin.charAt(0);

            if(column_origin.length() > 1) {
                moves.add(new Move(origin_block, EMPTY));
            }

            for(int j = 0; j < columns.size(); j++) {
                if(i == j) {
                    continue;
                }
                String column_destination = columns.get(j);
                char destination_block = column_destination.charAt(0);

                moves.add(new Move(origin_block, destination_block));
            }
        }

        return moves;
    }

    public State executeMove(Move move) {
        List<String> columns = new ArrayList<String>(
                Arrays.asList(state.split("\\|")));
        char origin_block = move.origin;
        char destination_block = move.destination;
        for(int i = 0; i < columns.size(); i++) {
            String column = columns.get(i);
            if(origin_block == column.charAt(0)) {
                column = column.substring(1);
                columns.set(i, column);
                break;
            }
        }
        if(destination_block == EMPTY) {
            columns.add(Character.valueOf(origin_block).toString());
        } else {
            for(int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                if(column.length() > 0
                        && destination_block == column.charAt(0)) {
                    column = origin_block + column;
                    columns.set(i, column);
                    break;
                }
            }
        }

        return new State(columns.toArray(new String[columns.size()]));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if((obj == null) || (!(obj instanceof State))) {
            return false;
        }

        State that = (State)obj;
        return state.equals(that.state);
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    public static class Move {

        private final char origin;

        private final char destination;

        private Move(char my_origin, char my_destination) {
            origin = my_origin;
            destination = my_destination;
        }

        @Override
        public String toString() {
            String str_origin = ((origin != EMPTY)
                    ? "Column which its topmost element is " + origin
                    : "Empty Column");
            String str_destination = ((destination != EMPTY)
                    ? "Column which its topmost element is " + destination
                    : "Empty Column");

            return "Move '" + str_origin + "' to '" + str_destination + "'";

        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) {
                return true;
            }
            if((obj == null) || (!(obj instanceof Move))) {
                return false;
            }

            Move that = (Move)obj;
            return (origin == that.origin) && (destination == that.destination);
        }

        @Override
        public int hashCode() {
            return Character.valueOf(origin).hashCode()
                    ^ Character.valueOf(destination).hashCode();
        }

    }

}
