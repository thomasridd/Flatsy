package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyQueryUriContains extends FlatsyQuery {
    String contains = null;

    public FlatsyQueryUriContains(String contains) {
        this.contains = contains.toLowerCase();
    }

    @Override
    public boolean matchesObject(FlatsyObject object) {
        return object.uri.toLowerCase().contains(this.contains);
    }


}
