package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyQueryUriContains implements FlatsyQuery {
    String contains = null;
    FlatsyQuery subQuery = null;
    boolean blacklister = false;
    boolean stopOnMatch = false;

    public FlatsyQueryUriContains(String contains) {
        this.contains = contains.toLowerCase();
    }

    @Override
    public boolean matchesObject(FlatsyObject object) {
        return object.uri.toLowerCase().contains(this.contains);
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
