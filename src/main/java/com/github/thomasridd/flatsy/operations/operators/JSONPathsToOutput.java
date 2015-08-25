package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by thomasridd on 18/08/15.
 *
 * Migrate
 *
 * Copies the object and contents to a second FlatsyDatabase instance
 *
 * Use for backup, partial backup, or
 *
 */
public class JSONPathsToOutput implements FlatsyOperator {
    OutputStream stream;
    List<String> paths;

    public JSONPathsToOutput(OutputStream stream, List<String> paths) {
        this.stream = stream; this.paths = paths;
    }

    @Override
    public void apply(FlatsyObject object) {
        try {
            String objectString = "";
            DocumentContext context = JsonPath.parse(object.retrieveStream());

            for (String path: paths) {
                try {
                    String value = context.read(path);
                    value = value.replace("\n", " ").replace("\r", " ");
                    objectString += value + "\t";
                } catch (com.jayway.jsonpath.PathNotFoundException e) {
                    objectString += "\t";
                }
            }

            stream.write((objectString + "\n").getBytes());
        } catch (IOException e) {
            System.out.println("Failed to print for uri: " + object.uri);
        }
    }
}
