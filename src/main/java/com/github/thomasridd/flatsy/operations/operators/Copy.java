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
 * Migrate
 *
 * Copies the object and contents to a second FlatsyDatabase instance
 *
 * Use for backup, partial backup, or
 *
 */
public class Copy implements FlatsyOperator {
    FlatsyDatabase db;
    boolean copyFolder = false;

    /**
     * Create copy object to an alternate database
     *
     * @param db any alternate Flatsy database
     */
    public Copy(FlatsyDatabase db) {
        this.db = db;
    }

    /**
     * Create copy object to an alternate database
     *
     * @param db any alternate Flatsy database
     * @param copyFolder copy all folder contents
     */
    public Copy(FlatsyDatabase db, boolean copyFolder) {
        this.db = db; this.copyFolder = copyFolder;
    }

    /**
     * Apply the copy operation
     *
     * @param object to a specific object
     */
    @Override
    public void apply(FlatsyObject object) {
        if (object.getType() == FlatsyObjectType.Folder || object.getType() == FlatsyObjectType.Null) { return; }

        List<FlatsyObject> objectList = new ArrayList<>();
        if (copyFolder) {
            for (FlatsyObject sibling: object.parent().children()) {
                objectList.add(sibling);
            }
        } else {
            objectList.add(object);
        }

        for(FlatsyObject copyObject: objectList) {
            if (copyObject.getType() != FlatsyObjectType.Folder) {
                db.delete(copyObject);
                try (InputStream stream = copyObject.retrieveStream()) {
                    db.create(copyObject, stream);
                } catch (IOException e) {
                    System.out.println("could not copy: " + copyObject.uri);
                }
            }
        }
    }
}
