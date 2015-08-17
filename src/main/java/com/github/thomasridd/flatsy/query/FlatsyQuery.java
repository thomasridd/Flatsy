package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;

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
    FlatsyQuery subQuery = null;
    boolean blacklister = false;
    boolean stopOnMatch = false;

    public FlatsyQuery query(FlatsyQuery query) {
        if (this.subQuery == null) {
            this.subQuery = query;
        } else {
            FlatsyQuery parent = this.subQuery;
            while(parent.subQuery != null) {
                parent = parent.subQuery;
            }
            parent.subQuery = query;
        }
        return this;
    }
    public FlatsyQuery query(String query) {
        return query(FlatsyQueryInterpreter.queryStringToFlatsyQuery(query));
    }

    public boolean matchesObject(FlatsyObject object) {
        return true;
    };

    public void setSubQuery(FlatsyQuery subQuery) {
        this.subQuery = subQuery;
    }

    public FlatsyQuery getSubQuery() {
        return this.subQuery;
    }

    public boolean getBlacklister() {
        return this.blacklister;
    }

    public void setBlackLister(boolean blackLister) {
        this.blacklister = blackLister;
    }

    public boolean getStopOnMatch() {
        return stopOnMatch;
    }

    public void setStopOnMatch(boolean stopOnMatch) {
        this.stopOnMatch = stopOnMatch;
    }

}
