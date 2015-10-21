package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;
import com.github.thomasridd.flatsy.util.FlatsyUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
public class CopyTo implements FlatsyOperator {
    FlatsyDatabase db;
    String newUriExpression;
    boolean copyFolder = false;

    /**
     * Create copy object to any database with modified uri
     *
     * @param db
     * @param newUriExpression
     */
    public CopyTo(FlatsyDatabase db, String newUriExpression) {
        this.db = db;
        this.newUriExpression = newUriExpression;
    }

    /**
     * Create copy object to any database with modified uri
     *
     * @param db any alternate Flatsy database
     * @param newUriExpression copy all folder contents
     */
    public CopyTo(FlatsyDatabase db, String newUriExpression, boolean copyFolder) {
        this.db = db; this.newUriExpression = newUriExpression; this.copyFolder = copyFolder;
    }

    /**
     * Apply the copy operation
     *
     * @param object to a specific object
     */
    @Override
    public void apply(FlatsyObject object) {
        if (object.getType() == FlatsyObjectType.Folder || object.getType() == FlatsyObjectType.Null) { return; }

        // Get all objects we need to be copying
        List<FlatsyObject> objectList = new ArrayList<>();
        if (copyFolder) {
            for (FlatsyObject sibling: object.parent().children()) {
                objectList.add(sibling);
            }
        } else {
            objectList.add(object);
        }

        // For each object
        for(FlatsyObject copyObject: objectList) {
            // If it is not a folder
            if (copyObject.getType() != FlatsyObjectType.Folder) {

                try {
                    // Get the new destination
                    String destination = FlatsyUtil.stringExpression(this.newUriExpression, copyObject);
                    FlatsyObject destinationObject = new FlatsyObject(destination, this.db);

                    // check we aren't copying the file to itself
                    if (!destination.equalsIgnoreCase(copyObject.uri) || !object.db.toString().equalsIgnoreCase(this.db.toString())) {
                        // clear where we are about to write
                        db.delete(destinationObject);
                        // and copy
                        try (InputStream stream = copyObject.retrieveStream()) {
                            db.create(destinationObject, stream);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
