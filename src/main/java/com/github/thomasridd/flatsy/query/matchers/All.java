package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class All implements FlatsyMatcher {
    
    @Override
    public boolean matches(FlatsyObject object) {
        return true;
    }


}
