package ua.edu.cdu.vu.price.aggregator.api.cache;

import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;

public interface ActionsUrlCacheManager {

    default Optional<Map.Entry<List<DslExpression<Void>>, String>> findUrlByActions(List<DslExpression<Void>> actions) {
        var mutableActions = new LinkedList<>(actions);
        var remainingActions = new LinkedList<DslExpression<Void>>();
        var listIterator = mutableActions.listIterator(mutableActions.size());
        while (listIterator.hasPrevious()) {
            var currentAction = listIterator.previous();
            String url = getUrlByActions(mutableActions);
            if (nonNull(url)) {
                return Optional.of(Map.entry(remainingActions, url));
            }
            remainingActions.addFirst(currentAction);
            listIterator.remove();
        }

        return Optional.empty();
    }

    String getUrlByActions(List<DslExpression<Void>> actions);

    void put(List<DslExpression<Void>> actions, String url);
}
