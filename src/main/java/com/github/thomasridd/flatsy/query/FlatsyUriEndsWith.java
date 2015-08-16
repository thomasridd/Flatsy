package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyUriEndsWith implements FlatsyQuery {
    String endString = null;

    public FlatsyUriEndsWith(String endString) {
        this.endString = endString.toLowerCase();
    }

    @Override
    public boolean matchesObject(FlatsyObject object) {
        return object.uri.endsWith(endString);
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
