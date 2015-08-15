package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.query.FlatsyQuery;
import com.github.thomasridd.flatsy.update.FlatsyUpdate;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyObject {
    FlatsyFlatFileDatabase db;
    String uri;

    public FlatsyObject(String uri, FlatsyFlatFileDatabase db) {
        this.uri = uri;
        this.db = db;
    }

    public FlatsyObjectType getType() {
        return FlatsyObjectType.JSONFile;
    }

    public String get() {
        return "";
    }
    public void write(String content) {

    }
    public void update(FlatsyUpdate update) {

    }
    public void delete() {

    }

    public FlatsyCursor query(FlatsyQuery flatsyQuery) {
        return flatsyQuery.runWithRoot(this);
    }
}
