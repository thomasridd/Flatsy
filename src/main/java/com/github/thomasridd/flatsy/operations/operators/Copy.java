package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;

import java.io.IOException;
import java.io.InputStream;

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

    public Copy(FlatsyDatabase db) {
        this.db = db;
    }

    @Override
    public void apply(FlatsyObject object) {
        if (object.getType() == FlatsyObjectType.Folder || object.getType() == FlatsyObjectType.Null) { return; }

        db.delete(object);
        try (InputStream stream = object.retrieveStream()) {
            db.create(object, stream);
        } catch (IOException e) {
            System.out.println("could not backup: " + object.uri);
        }
    }
}
