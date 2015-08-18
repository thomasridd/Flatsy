package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;
import com.github.thomasridd.flatsy.query.FlatsyQuery;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class IsFile implements FlatsyMatcher {
    
    @Override
    public boolean matches(FlatsyObject object) {
        return object.getType() == FlatsyObjectType.JSONFile || object.getType() == FlatsyObjectType.OtherFile;
    }


}
