package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom.Ridd on 15/08/15.
 *
 * FlatsyCursor cursor = FlatsyQuery.query();
 */
public class FlatsyCursor {
    FlatsyQuery query = null;
    FlatsyCursor subCursor = null;


    int depth = 0; // these should probably be optimised
    List<FlatsyObject> tree = new ArrayList<>();
    List<List<FlatsyObject>> nodes = new ArrayList<>();
    List<Integer> treePosition = new ArrayList<>();

    public FlatsyCursor(FlatsyObject cursorRoot, FlatsyQuery query) {
        tree.add(cursorRoot);
        nodes.add(cursorRoot.children());
        treePosition.add(-1);
        this.query = query;
    }
    public FlatsyCursor(FlatsyObject cursorRoot) {
        tree.add(cursorRoot);
        nodes.add(cursorRoot.children());
        treePosition.add(-1);
        this.query = null;
    }

    public FlatsyObject currentObject() {
        if (subCursor != null) {
            return subCursor.currentObject();
        } else {
            return nodes.get(depth).get(treePosition.get(depth));
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
        // Option 1. We are currently working through a subquery
        if (subCursor != null) {
            if (subCursor.next()) {
                return true;
            } else {
                subCursor = null;
            }
        }

        if (onNextStepMoveDownTree) {
            FlatsyObject current = nodes.get(depth).get(treePosition.get(depth));
            tree.add(current);
            nodes.add(current.children());
            treePosition.add(-1);
            depth += 1;
            onNextStepMoveDownTree = false;
        }

        // Option 2. We are walking our own tree
        if (!(treePosition.get(depth) < nodes.get(depth).size() - 1)) {
            // Option i) We have exhausted this node on the tree - step back
            treePosition.remove(treePosition.size() - 1);
            nodes.remove(nodes.size() - 1);
            tree.remove(tree.size() - 1);
            depth -=1;
            return false; // this step has just been to move on the file tree
        } else {
            // Option ii) Stay at this depth
            // move one step on
            treePosition.set(depth, treePosition.get(depth) + 1); // Advance on this node

            FlatsyObject current = nodes.get(depth).get(treePosition.get(depth));
            if (this.query.matchesObject(current)) { // If our query matches
                if (this.query.getBlacklister()) {
                    // this node is poison - move on
                    return false;
                } else {
                    // this node is good
                    if (this.query.getSubQuery() == null) {
                        onNextStepMoveDownTree = (!this.query.getStopOnMatch() && current.getType() == FlatsyObjectType.Folder); // If this isn't the end drop down the file tree
                        return true; // Found a result! A final result! Tell the world true
                    } else {
                        this.subCursor = new FlatsyCursor(current, this.query.getSubQuery());
                        return false; // the file walk now steps to the next query level
                    }
                }
            } else {
                if (this.query.getBlacklister()) {
                    // this node isn't blacklisted
                    if (this.query.getSubQuery() == null) {
                        onNextStepMoveDownTree = (!this.query.getStopOnMatch() && current.getType() == FlatsyObjectType.Folder); // If this isn't the end drop down the file tree
                        return true; // Found a result! A final result! Tell the world true
                    } else {
                        this.subCursor = new FlatsyCursor(current, this.query.getSubQuery());
                        return false; // the file walk now steps to the next query level
                    }
                } else {
                    // this node just hasn't been matched
                    onNextStepMoveDownTree = current.getType() == FlatsyObjectType.Folder;
                    return false;
                }
            }
        }
    }

    public FlatsyCursor query(FlatsyQuery newQuery) {
        if (this.query == null) {
            this.query = newQuery;
        } else {
            FlatsyQuery tryQuery = this.query;
            while (tryQuery.getSubQuery() != null) {
                tryQuery = tryQuery.getSubQuery();
            }
            tryQuery.setSubQuery(newQuery);
        }
        return this;
    }

    public FlatsyCursor query(String queryString) {
        return query(FlatsyQueryInterpreter.queryStringToFlatsyQuery(queryString));
    }
}
