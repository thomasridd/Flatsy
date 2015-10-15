package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;

/**
 * Created by Tom.Ridd on 15/08/15.
 *
 * Matcher implementing logical OR
 *
 */
public class Or implements FlatsyMatcher {
    FlatsyMatcher m1;
    FlatsyMatcher m2;

    public Or(FlatsyMatcher matcher1, FlatsyMatcher matcher2) {
        this.m1 = matcher1;
        this.m2 = matcher2;
    }

    @Override
    public boolean matches(FlatsyObject object) {
        return m1.matches(object) || m2.matches(object);
    }


}
