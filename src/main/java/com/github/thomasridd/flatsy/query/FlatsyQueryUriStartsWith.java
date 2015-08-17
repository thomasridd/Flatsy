package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyQueryUriStartsWith extends FlatsyQuery {
    String startString = null;

    public FlatsyQueryUriStartsWith(String startString) {
        this.startString = startString.toLowerCase();
    }

    @Override
    public boolean matchesObject(FlatsyObject object) {
        return object.uri.toLowerCase().startsWith(this.startString);
    }
}
