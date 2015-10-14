package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class UriEndsWith implements FlatsyMatcher {
    String endString = null;

    public UriEndsWith(String endString) {
        this.endString = endString.toLowerCase();
    }

    @Override
    public boolean matches(FlatsyObject object) {
        return object.uri.endsWith(endString);
    }

}
