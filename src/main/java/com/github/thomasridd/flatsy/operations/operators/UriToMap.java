package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentMap;

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
public class UriToMap implements FlatsyOperator {
    ConcurrentMap<String, String> map;
    String value;

    public UriToMap(ConcurrentMap<String, String> map, String value) {
        this.map = map;
        this.value = value;
    }

    @Override
    public void apply(FlatsyObject object) {
        map.put(object.uri.toString(), value);
    }
}
