package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyQueryUriStartsWith implements FlatsyQuery {
    String startString = null;
    FlatsyQuery subQuery = null;
    boolean blacklister = false;
    boolean stopOnMatch = false;

    public FlatsyQueryUriStartsWith(String startString) {
        this.startString = startString.toLowerCase();
    }

    @Override
    public boolean matchesObject(FlatsyObject object) {
        return object.uri.startsWith(startString);
    }

    @Override
    public void setSubQuery(FlatsyQuery subQuery) {
        this.subQuery = subQuery;
    }

    @Override
    public FlatsyQuery getSubQuery() {
        return this.subQuery;
    }

    @Override
    public boolean getBlacklister() {
        return this.blacklister;
    }

    @Override
    public void setBlackLister(boolean blackLister) {
        this.blacklister = blackLister;
    }

    @Override
    public boolean getStopOnMatch() {
        return stopOnMatch;
    }

    @Override
    public void setStopOnMatch(boolean stopOnMatch) {
        this.stopOnMatch = stopOnMatch;
    }

}
