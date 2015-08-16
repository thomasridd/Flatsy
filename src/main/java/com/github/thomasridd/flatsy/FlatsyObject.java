package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.query.FlatsyQuery;
import com.github.thomasridd.flatsy.update.FlatsyUpdate;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyObject implements Comparable {
    FlatsyDatabase db;
    String uri;

    public FlatsyObject(String uri, FlatsyDatabase db) {
        this.uri = uri.toLowerCase();
        this.db = db;
    }

    public FlatsyObjectType getType() {
        return db.type(this.uri);
    }

    public FlatsyCursor query(FlatsyQuery flatsyQuery) {
        return flatsyQuery.runWithRoot(this);
    }

    @Override
    public int compareTo(Object o) {
        FlatsyObject obj = (FlatsyObject) o;
        return this.uri.compareTo(obj.uri);
    }
}
