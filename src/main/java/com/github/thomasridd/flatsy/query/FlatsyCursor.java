package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.operations.operators.FlatsyOperator;
import com.github.thomasridd.flatsy.operations.FlatsyWorker;
import com.github.thomasridd.flatsy.query.matchers.FlatsyMatcher;
import com.github.thomasridd.flatsy.query.matchers.FlatsyMatcherBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom.Ridd on 15/08/15.
 *
 * FlatsyCursor cursor = FlatsyQuery.query();
 */
public class FlatsyCursor {
    FlatsyQuery query = null;


    int depth = 0; // these should probably be optimised
    List<FlatsyObject> tree = new ArrayList<>();
    List<List<FlatsyObject>> nodes = new ArrayList<>();
    List<Integer> treePosition = new ArrayList<>();

    public FlatsyCursor(FlatsyObject cursorRoot, FlatsyQuery query) {
        this(cursorRoot);
        this.query = query;
    }
    public FlatsyCursor(FlatsyObject cursorRoot) {
        tree.add(cursorRoot);
        List<FlatsyObject> asList = new ArrayList<>(); asList.add(cursorRoot);

        nodes.add(asList);
        treePosition.add(-1);
        this.query = null;
    }

    public void apply(FlatsyOperator operation) {
        FlatsyWorker worker = new FlatsyWorker(operation);
        worker.updateAll(this);
    }

    public FlatsyObject currentObject() {
        try {
            return nodes.get(depth).get(treePosition.get(depth));
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean next() {
        boolean found = false;
        while(depth >= 0) {
            found = takeStep();

            if(found) return true;
        }
        return false;
    }

    boolean onNextStepMoveDownTree = false;

    boolean takeStep() {

        if (onNextStepMoveDownTree) {
            stepDownTree();
            onNextStepMoveDownTree = false;
        }

        // Option 2. We are walking our own tree

        // Option i) We have exhausted this node on the tree
        if (!(treePosition.get(depth) < nodes.get(depth).size() - 1)) {
            // Step back up the tree
            stepUpTree();
            return false; // this step has just been to move on the file tree
        } else {
        // Option ii) Stay at this depth
            treePosition.set(depth, treePosition.get(depth) + 1); // Advance on this node
            // Now deal with the new node
            FlatsyObject current = nodes.get(depth).get(treePosition.get(depth));

            FlatsyQueryResult result = query.checkNode(current);

            if (result == FlatsyQueryResult.Blocked) {
                return false;
            } else if( result == FlatsyQueryResult.MatchThenBlock) {
                onNextStepMoveDownTree = false;
                return true;
            } else if( result == FlatsyQueryResult.Match) {
                onNextStepMoveDownTree = true;
                return true;
            } else {
                onNextStepMoveDownTree = true;
                return false;
            }
        }
    }

    private void stepUpTree() {
        treePosition.remove(treePosition.size() - 1);
        nodes.remove(nodes.size() - 1);
        tree.remove(tree.size() - 1);
        depth -=1;
    }
    private void stepDownTree() {
        FlatsyObject current = nodes.get(depth).get(treePosition.get(depth));
        tree.add(current);
        nodes.add(current.children());
        treePosition.add(-1);
        depth += 1;
    }
    public FlatsyCursor query(FlatsyQueryType type, FlatsyMatcher matcher) {
        if (this.query == null) {
            this.query = new FlatsyQuery().query(type, matcher);
        } else {
            this.query.query(type, matcher);
        }
        return this;
    }

    public FlatsyCursor query(FlatsyMatcher matcher) {
        return query(FlatsyQueryType.Condition, matcher);
    }

    public FlatsyCursor query(String queryString) {
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
