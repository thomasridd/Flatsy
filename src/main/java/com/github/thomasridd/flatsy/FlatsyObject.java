package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.operations.operators.FlatsyOperator;
import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.query.FlatsyQuery;
import com.github.thomasridd.flatsy.query.FlatsyQueryType;
import com.github.thomasridd.flatsy.query.matchers.All;
import com.github.thomasridd.flatsy.query.matchers.FlatsyMatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Tom.Ridd on 15/08/15.
 *
 * Base object for Flatsy system
 *
 * Can be a directory or file
 */
public class FlatsyObject implements Comparable {
    public FlatsyDatabase db;
    public String uri;

    public FlatsyObject(String uri, FlatsyDatabase db) {
        this.uri = uri.toLowerCase();
        if (this.uri.startsWith("/")) {
            this.uri = this.uri.substring(1);
        }
        this.db = db;
    }

    public FlatsyObjectType getType() {
        return db.type(this.uri);
    }

    public FlatsyCursor query(FlatsyMatcher flatsyMatcher) {
        return new FlatsyCursor(this, new FlatsyQuery(flatsyMatcher));
    }
    public FlatsyCursor query(FlatsyQueryType type, FlatsyMatcher flatsyMatcher) {
        return new FlatsyCursor(this, new FlatsyQuery(type, flatsyMatcher));
    }
    public FlatsyCursor query(String flatsyQuery) {
        return new FlatsyCursor(this, new FlatsyQuery(flatsyQuery));
    }

    public FlatsyCursor cursor() {
        return new FlatsyCursor(this, new FlatsyQuery(new All()));
    }

    public void apply(FlatsyOperator operator) {
        this.cursor().apply(operator);
    }

    public String retrieve() throws IOException {
        return db.retrieve(this);
    }
    public InputStream retrieveStream() throws IOException {
        return db.retrieveStream(this);
    }
    public void create(String content) {
        db.create(this, content);
    }
    public void create(InputStream stream) throws IOException {
        db.create(this, stream);
    }

    public List<FlatsyObject> children() {
        return db.children(this);
    }

    public FlatsyObject parent () {
        return db.parent(this);
    }

    @Override
    public int compareTo(Object o) {
        FlatsyObject obj = (FlatsyObject) o;
        return this.uri.compareTo(obj.uri);
    }
}
