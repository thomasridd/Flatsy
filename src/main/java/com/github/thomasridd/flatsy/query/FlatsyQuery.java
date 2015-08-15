package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public interface FlatsyQuery {
    FlatsyQuery subQuery = null;

    public FlatsyCursor runWithRoot(FlatsyObject object);

}
