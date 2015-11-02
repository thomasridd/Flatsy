package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.util.FlatsyUtil;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.io.IOException;

/**
 * Created by thomasridd on 18/08/15.
 *
 * Deletes a JSONPath from an object
 *
 */
public class JSONPathRemove implements FlatsyOperator {
    String jsonPath;
    String field;

    public JSONPathRemove(String jsonPath, String field) {
        this.jsonPath = jsonPath;
        this.field = field;
    }

    @Override
    public void apply(FlatsyObject object) {
        try {
            Object value;

            DocumentContext context = JsonPath.parse(object.retrieveStream());
            try {
                context.delete(jsonPath + "." + field);
            } catch (Exception e) {
                System.out.println("Error in delete " + jsonPath + "." + field + " from " + object.uri);
            }
            object.create(context.jsonString());
        } catch (IOException e) {
            System.out.println("Failed to print for uri: " + object.uri);
        }
    }
}
