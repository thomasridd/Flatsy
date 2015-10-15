package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;
import com.google.gson.Gson;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.util.List;

/**
 * Created by Tom.Ridd on 15/08/15.
 *
 * Matches internally valid JSON Objects
 */
public class JSONValid implements FlatsyMatcher {
    private static final Gson gson = new Gson();

    @Override
    public boolean matches(FlatsyObject object) {
        if (object.getType() == FlatsyObjectType.Folder) { return false; } // This only applies to files

            try {
                gson.fromJson(object.retrieve(), Object.class);
                return true;
            } catch(com.google.gson.JsonSyntaxException ex) {
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
    }
}
