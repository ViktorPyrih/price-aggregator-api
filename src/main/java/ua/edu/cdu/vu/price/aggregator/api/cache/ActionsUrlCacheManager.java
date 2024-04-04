package ua.edu.cdu.vu.price.aggregator.api.cache;

import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;

public interface ActionsUrlCacheManager {

    default Optional<Map.Entry<List<DslExpression<Void>>, String>> findUrlByActionsAndContext(List<DslExpression<Void>> actions, Map<String, Object> context) {
        var mutableActions = new LinkedList<>(actions);
        var remainingActions = new LinkedList<DslExpression<Void>>();
        var listIterator = mutableActions.listIterator(mutableActions.size());
        while (listIterator.hasPrevious()) {
            var currentAction = listIterator.previous();
            String url = getUrlByActions(serialize(mutableActions, context));
            if (nonNull(url)) {
                return Optional.of(Map.entry(remainingActions, url));
            }
            remainingActions.addFirst(currentAction);
            listIterator.remove();
        }

        return Optional.empty();
    }

    default void put(List<DslExpression<Void>> actions, String url, Map<String, Object> context) {
        putActions(serialize(actions, context), url);
    }

    String getUrlByActions(List<String> actions);

    void putActions(List<String> actions, String url);

    void clear();

    private List<String> serialize(List<DslExpression<Void>> actions, Map<String, Object> context) {
        return actions.stream()
                .map(action -> action.toString(context))
                .toList();
    }
}
