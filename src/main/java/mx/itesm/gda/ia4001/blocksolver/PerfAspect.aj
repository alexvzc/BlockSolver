/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4001.blocksolver;

import java.util.Deque;
import java.util.List;
import java.util.Set;
import static java.text.MessageFormat.format;
import static mx.itesm.gda.ia4001.blocksolver.Searcher.SearchType;
/**
 *
 * @author alexv
 */

privileged public aspect PerfAspect percflow(searchExec(SearchType)) {

    private long startTime;

    private int generatedStates = 0;

    private int visitedStates = 0;

    private int alreadyVisitedStates = 0;

    private int maximumSize = Integer.MIN_VALUE;

    pointcut searchExec(SearchType type) :
        execution(List Searcher.searchSolution(SearchType))
        && args(type);

    pointcut generateState() : call(* Deque.add*(*))
            && cflow(searchExec(SearchType));

    pointcut visitState() : call(* Deque.removeFirst())
            && cflow(searchExec(SearchType));

    pointcut checkStateQueueSize() : call(int Deque.size())
            && cflow(searchExec(SearchType));

    pointcut validateVisitedState() : call(boolean Set.contains(*))
            && cflow(searchExec(SearchType));

    before() : searchExec(SearchType) {
        startTime = System.currentTimeMillis();
    }

    after(SearchType type) returning (List l) : searchExec(type) {
        long completionTime = System.currentTimeMillis() - startTime;

        Searcher.LOGGER.info(
                format("Solution found in {0} steps in {1} milliseconds\n"
                + "Visited {2} states out of {3} generated states\n"
                + "Maximum enqueued states to visit {4}\n"
                + "Generated {5} duplicated states",
                l.size(), completionTime, visitedStates,
                generatedStates, maximumSize, alreadyVisitedStates));
    }

    after() returning : generateState() {
        generatedStates++;
    }

    after() returning : visitState() {
        visitedStates++;
    }

    after() returning (boolean visited) : validateVisitedState() {
        if(visited) {
            alreadyVisitedStates++;
        }
    }

    after() returning (int size) : checkStateQueueSize() {
        if(size > maximumSize) {
           maximumSize = size;
        }
    }
}
