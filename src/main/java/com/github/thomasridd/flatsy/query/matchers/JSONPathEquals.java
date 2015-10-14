package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class JSONPathEquals implements FlatsyMatcher {
    String jsonPath = null;
    String equalTo = null;

    public JSONPathEquals(String jsonPath, String equalTo) {
        this.jsonPath = jsonPath;
        this.equalTo = equalTo;
    }

    @Override
    public boolean matches(FlatsyObject object) {
        if (object.getType() == FlatsyObjectType.Folder) { return false; } // This only applies to files

        try {
            DocumentContext context = JsonPath.parse(object.retrieveStream());
            String value = context.read(this.jsonPath);
            return value.equalsIgnoreCase(this.equalTo);
        } catch (IOException e) {
            System.out.println("Could not assess " + object.uri + " for " + jsonPath + " equals " + equalTo);
            return false;
        } catch (com.jayway.jsonpath.PathNotFoundException e) {
            return false;
        }
    }
}
