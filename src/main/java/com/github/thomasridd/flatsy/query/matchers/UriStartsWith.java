package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.query.FlatsyQuery;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class UriStartsWith implements FlatsyMatcher {
    String startString = null;

    public UriStartsWith(String startString) {
        this.startString = startString.toLowerCase();
    }

    @Override
    public boolean matches(FlatsyObject object) {
        return object.uri.toLowerCase().startsWith(this.startString);
    }
}
