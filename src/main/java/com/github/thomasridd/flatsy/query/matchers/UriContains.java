package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class UriContains implements FlatsyMatcher {
    String contains = null;

    public UriContains(String contains) {
        this.contains = contains.toLowerCase();
    }

    @Override
    public boolean matches(FlatsyObject object) {
        return object.uri.toLowerCase().contains(this.contains);
    }


}
