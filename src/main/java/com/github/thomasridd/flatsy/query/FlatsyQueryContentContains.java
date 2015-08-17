package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyQueryContentContains extends FlatsyQuery {
    String containsString = null;

    public FlatsyQueryContentContains(String containsString) {
        this.containsString = containsString.toLowerCase();
    }

    @Override
    public boolean matchesObject(FlatsyObject object) {
        if (object.getType() == FlatsyObjectType.Folder) { return false; } // This only applies to files

        boolean found = false;
        try {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(object.db.retrieveStream(object)))) {
                String line;
                while(((line = br.readLine()) != null) && !found) {
                    // process the line.
                    found = line.contains(containsString);
                }
            }
            return found;
        } catch (IOException e) {
            return false;
        }
    }
}
