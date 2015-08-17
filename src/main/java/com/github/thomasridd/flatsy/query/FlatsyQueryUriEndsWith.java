package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyQueryUriEndsWith extends FlatsyQuery {
    String endString = null;

    public FlatsyQueryUriEndsWith(String endString) {
        this.endString = endString.toLowerCase();
    }

    @Override
    public boolean matchesObject(FlatsyObject object) {
        return object.uri.endsWith(endString);
    }

}
