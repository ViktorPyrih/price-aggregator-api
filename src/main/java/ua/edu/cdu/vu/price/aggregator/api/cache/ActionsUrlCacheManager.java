package ua.edu.cdu.vu.price.aggregator.api.cache;

import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;

public interface ActionsUrlCacheManager {

    default Optional<Map.Entry<Integer, String>> findUrlByActionsAndContext(List<DslExpression<Void>> actions, Map<String, Object> context) {
        var mutableActions = new LinkedList<>(actions);
        int currentIndex = mutableActions.size() - 1;
        var listIterator = mutableActions.listIterator(mutableActions.size());
        while (listIterator.hasPrevious()) {
            listIterator.previous();
            String url = getUrlByActions(serialize(mutableActions, context));
            if (nonNull(url)) {
                return Optional.of(Map.entry(currentIndex, url));
            }
            currentIndex--;
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
