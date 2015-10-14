package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
public class JSONObjectsToOutput implements FlatsyOperator {
    OutputStream stream;
    List<String> subpaths;
    String objectPath;

    public JSONObjectsToOutput(OutputStream stream, String objectPath, List<String> subpaths) {
        this.stream = stream; this.subpaths = subpaths; this.objectPath = objectPath;
    }

    @Override
    public void apply(FlatsyObject object) {
        try {

            DocumentContext context = JsonPath.parse(object.retrieveStream());


                try {
                    JSONArray objects = context.read(objectPath);

                    for (Object arrayObject: objects) {
                        String objectString = object.uri + "\t";


                        HashMap<String, String> hashMap = (LinkedHashMap) arrayObject;
                        for (String path: subpaths) {
                            if (hashMap.keySet().contains(path)) {
                                String value = hashMap.get(path).toString().replace("\n", " ").replace("\r", " ");
                                objectString += value + "\t";
                            } else {
                                objectString += "\t";
                            }
                        }

                        stream.write((objectString + "\n").getBytes());
                    }
                } catch (com.jayway.jsonpath.PathNotFoundException e) {

                }

        } catch (IOException e) {
            System.out.println("Failed to print for uri: " + object.uri);
        }
    }
}
