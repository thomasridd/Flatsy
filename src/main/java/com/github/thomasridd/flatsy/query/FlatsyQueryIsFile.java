package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyQueryIsFile extends FlatsyQuery {
    
    @Override
    public boolean matchesObject(FlatsyObject object) {
        return object.getType() == FlatsyObjectType.JSONFile || object.getType() == FlatsyObjectType.OtherFile;
    }


}
