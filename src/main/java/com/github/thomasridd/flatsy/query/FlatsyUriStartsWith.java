package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyUriStartsWith implements FlatsyQuery {
    String startString = null;

    public FlatsyUriStartsWith(String startString) {
        this.startString = startString.toLowerCase();
    }

    @Override
    public boolean matchesObject(FlatsyObject object) {
        return object.uri.startsWith(startString);
    }

    @Override
    public boolean isBlackLister() {
        return blackLister;
    }

    @Override
    public boolean shouldStopOnMatch() {
        return stopOnMatch;
    }

}
