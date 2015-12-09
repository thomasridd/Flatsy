package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class SingleFile implements FlatsyMatcher {
    String uri = null;

    public SingleFile(String uri) {
        this.uri = uri.toLowerCase();
    }

    @Override
    public boolean matches(FlatsyObject object) {
        return object.uri.toLowerCase().equalsIgnoreCase(uri);
    }


}
