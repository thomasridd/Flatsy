package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.query.matchers.FlatsyMatcher;
import com.github.thomasridd.flatsy.query.matchers.FlatsyMatcherBuilder;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tom.Ridd on 15/08/15.
 *
 * Flatsy implements a hierarchical search
 *
 * BlackLister indicates that this is a blacklist query
 *
 * A blackLister query will ignore matching nodes all child nodes
 *
 * A stopOnMatch query will not search subtrees once it has found a result
 *      useful if you want a list of directories that contain the word "economy" for example but don't want to sort through objects
 *
 */
public class FlatsyQuery {

    List<FlatsyMatcher> conditions = new ArrayList<>();
    List<FlatsyMatcher> halfBlockers = new ArrayList<>();
    List<FlatsyMatcher> blockers = new ArrayList<>();

    public FlatsyQuery() {

    }
    public FlatsyQuery(FlatsyQueryType type, FlatsyMatcher matcher) {
        query(type, matcher);
    }
    public FlatsyQuery(String string) {
        query(string);
    }
    public FlatsyQuery(FlatsyMatcher matcher) {
        query(FlatsyQueryType.Condition, matcher);
    }

    public FlatsyQueryResult checkNode(FlatsyObject object) {
        // Check if node blocked
        for (FlatsyMatcher blocker: blockers) {
            if (blocker.matches(object)) { return FlatsyQueryResult.Blocked; }
        }

        // Check if node is half blocked (i.e. you should include it but ignore all children
        for (FlatsyMatcher halfBlocker: this.halfBlockers) {
            if (halfBlocker.matches(object)) {
                for (FlatsyMatcher condition: this.conditions) {
                    if (!condition.matches(object)) {
                        return FlatsyQueryResult.Blocked;
                    }
                }
                return FlatsyQueryResult.MatchThenBlock;
            }
        }

        // Check if the uri passes our conditions
        for (FlatsyMatcher condition: conditions) {
            if (!condition.matches(object)) {
                return FlatsyQueryResult.NoMatch;
            }
        }

        // All conditions passed return match
        return FlatsyQueryResult.Match;
    }

    public FlatsyQuery query(FlatsyQueryType type, FlatsyMatcher matcher) {
        if (type == FlatsyQueryType.Blocker) {
            blockers.add(matcher);
        } else if (type == FlatsyQueryType.ConditionBlocker) {
            halfBlockers.add(matcher);
        } else if (type == FlatsyQueryType.Condition) {
            conditions.add(matcher);
        }
        return this;
    }
    public FlatsyQuery query(FlatsyMatcher matcher) {
        return query(FlatsyQueryType.Condition, matcher);
    }
    public FlatsyQuery query(String queryString) {
        String cleanedString = queryString.toLowerCase();

        if (cleanedString.startsWith("block:")) {
            cleanedString = cleanedString.substring("block:".length());
            return query(FlatsyQueryType.Blocker, FlatsyMatcherBuilder.queryStringToMatcher(cleanedString));
        } else if (cleanedString.startsWith("stop:")) {
            cleanedString = cleanedString.substring("stop:".length());
            return query(FlatsyQueryType.ConditionBlocker, FlatsyMatcherBuilder.queryStringToMatcher(cleanedString));
        } else {
            return query(FlatsyQueryType.Condition, FlatsyMatcherBuilder.queryStringToMatcher(cleanedString));
        }
    }
}
