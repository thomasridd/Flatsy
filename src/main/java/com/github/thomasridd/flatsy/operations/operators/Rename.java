package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasridd on 18/08/15.
 *
 * Rename
 *
 * Changes an object's uri
 *
 */
public class Rename implements FlatsyOperator {
    String oldUri = "";
    String moveTo = "";

    /**
     *
     * @param oldUri all objects below this
     * @param moveTo
     */
    public Rename(String oldUri, String moveTo) {
        this.oldUri = oldUri; this.moveTo = moveTo;
    }

    /**
     * Apply the rename operation
     *
     * @param object to a specific object
     */
    @Override
    public void apply(FlatsyObject object) {
        if (object.getType() == FlatsyObjectType.Folder || object.getType() == FlatsyObjectType.Null) { return; }

        if (object.uri.startsWith(oldUri)) {
            String newUri = moveTo + object.uri.substring(oldUri.length());
            FlatsyObject newObject = object.db.get(newUri);
            try {

                newObject.create(object.retrieveStream());
                object.db.delete(object);

                deleteEmptyParents(object);
            } catch (IOException e) {
                System.out.println("Could not move Flatsy object " + object.uri);
                e.printStackTrace();
            }

        }

    }

    private void deleteEmptyParents(FlatsyObject object) {
        FlatsyObject folder = object.parent();
        if (folder.children().size() == 0) {
            folder.db.delete(folder);
            deleteEmptyParents(folder) ;
        };
    }
}
