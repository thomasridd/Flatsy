package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.query.FlatsyQuery;
import com.github.thomasridd.flatsy.update.FlatsyUpdate;

import java.util.List;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyObject implements Comparable {
    public FlatsyDatabase db;
    public String uri;

    public FlatsyObject(String uri, FlatsyDatabase db) {
        this.uri = uri.toLowerCase();
        this.db = db;
    }

    public FlatsyObjectType getType() {
        return db.type(this.uri);
    }

    public FlatsyCursor query(FlatsyQuery flatsyQuery) {
        return new FlatsyCursor(this, flatsyQuery);
    }

    public List<FlatsyObject> children() {
        return db.children(this);
    }

    @Override
    public int compareTo(Object o) {
        FlatsyObject obj = (FlatsyObject) o;
        return this.uri.compareTo(obj.uri);
    }
}
