package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.util.List;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class JSONPathAtLeastOne implements FlatsyMatcher {
    String jsonPath = null;

    public JSONPathAtLeastOne(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    @Override
    public boolean matches(FlatsyObject object) {
        if (object.getType() == FlatsyObjectType.Folder) { return false; } // This only applies to files

        try {
            DocumentContext context = JsonPath.parse(object.retrieveStream());
            List<Object> values = context.read(this.jsonPath);
            return values.size() >= 1;
        } catch (IOException e) {
            System.out.println("Could not assess " + object.uri + " for " + jsonPath);
            return false;
        } catch (com.jayway.jsonpath.PathNotFoundException e) {
            return false;
        }
    }
}
