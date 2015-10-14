package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.google.gson.Gson;

import java.io.IOException;

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
public class JSONValidate implements FlatsyOperator {
    private static final Gson gson = new Gson();
    public JSONValidate() {
    }

    @Override
    public void apply(FlatsyObject object) {
        try {
            if (!validateJSON(object)) {
                System.out.println("invalide json for " + object.uri);
            }
        } catch (IOException e) {
            System.out.println("Failed to print for uri: " + object.uri);
        }
    }

    public boolean validateJSON(FlatsyObject object) throws IOException {

                String json = object.retrieve();
                if (!isJSONValid(json)) {
                    return false;
                }
        return true;

    }

    public static boolean isJSONValid(String json) {
        try {
            gson.fromJson(json, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }
}
