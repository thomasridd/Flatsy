package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;

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
public class Migrate implements FlatsyOperator {
    FlatsyDatabase db;

    public Migrate(FlatsyDatabase db) {
        this.db = db;
    }

    @Override
    public void apply(FlatsyObject object) {

        try(InputStream stream = object.retrieveStream();) {
            db.create(object, stream);
        } catch (IOException e) {
            System.out.println("could not backup: " + object.uri);;
        }
    }
}
