package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;

/**
 * Created by Tom.Ridd on 14/10/15.
 */
public class Not implements FlatsyMatcher {
    FlatsyMatcher matcher;

    public Not(FlatsyMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(FlatsyObject object) {
        return !matcher.matches(object);
    }
}
