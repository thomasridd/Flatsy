package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.FlatsyObject;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;

/**
 * Created by Tom.Ridd on 15/08/15.
 *
 * Flatsy implements a hierarchical search
 *
 * BlackLister indicates that this is a blacklist query
 *
 * A blackLister query will ignore matching nodes all child nodes
 *
 * A stopOnMatch query will not search subtrees once it has found a result
 *      useful if you want a list of directories that contain the word "economy" for example but don't want to sort through objects
 *
 */
public interface FlatsyQuery {

    public boolean matchesObject(FlatsyObject object);

    public void setSubQuery(FlatsyQuery subQuery);
    public FlatsyQuery getSubQuery();

    public boolean getBlacklister();
    public void setBlackLister(boolean blackLister);

    public boolean getStopOnMatch();
    public void setStopOnMatch(boolean stopOnMatch);

}
