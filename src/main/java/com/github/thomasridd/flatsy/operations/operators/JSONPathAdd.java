package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.util.FlatsyUtil;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by thomasridd on 18/08/15.
 *
 * Adds
 *
 *
 */
public class JSONPathAdd implements FlatsyOperator {
    String jsonPath;
    String expression;
    String field;

    public JSONPathAdd(String jsonPath, String field, String expression) {
        this.jsonPath = jsonPath;
        this.expression = expression;
        this.field = field;
    }

    @Override
    public void apply(FlatsyObject object) {
        try {
            Object value;
            if (expression.startsWith("{") && expression.endsWith("}") || expression.startsWith("[") && expression.endsWith("]")) {
                value = JsonPath.parse(expression).json();
            } else {
                value = FlatsyUtil.stringOperation(this.expression, object);
            }

            DocumentContext context = JsonPath.parse(object.retrieveStream());
            try {
                context.add(JsonPath.compile(jsonPath + "." + field), value);
            } catch (PathNotFoundException e) {
                context.put(JsonPath.compile(jsonPath), field, JsonPath.parse("[]").json());
                context.add(JsonPath.compile(jsonPath + "." + field), value);
            }
            object.create(context.jsonString());
        } catch (IOException e) {
            System.out.println("Failed to print for uri: " + object.uri);
        }
    }
}
