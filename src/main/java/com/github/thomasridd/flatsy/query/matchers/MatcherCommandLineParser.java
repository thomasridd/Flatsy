package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.util.FlatsyUtil;

import java.util.List;

/**
 * Created by thomasridd on 14/10/15.
 */
public class MatcherCommandLineParser {

    /**
     *
     *
     * @param db
     * @param filters
     * @return
     */
    public static FlatsyCursor cursorFromFilterCommands(FlatsyDatabase db, List<String> filters) {
        FlatsyCursor cursor = db.root().cursor();
        for (String filter: filters) {
            cursor = applyFilterToCursor(cursor, filter);
        }
        return cursor;
    }

    protected static FlatsyCursor applyFilterToCursor(FlatsyCursor cursor, String filter) {
        List<String> args = FlatsyUtil.commandArguments(filter);

        if (args.get(1).equalsIgnoreCase("files")) {
            return cursor.query(new IsFile());
        } else if (args.get(1).equalsIgnoreCase("folders")) {
            return cursor.query(new IsFolder());
        } else if (args.get(1).equalsIgnoreCase("uri_contains")) {
            return cursor.query(new UriContains(args.get(2)));
        } else if (args.get(1).equalsIgnoreCase("uri_ends")) {
            return cursor.query(new UriEndsWith(args.get(2)));
        } else if (args.get(1).equalsIgnoreCase("jsonpath_exists")) {
            return cursor.query(new JSONPathExists(args.get(2)));
        } else if (args.get(1).equalsIgnoreCase("jsonpath_equals")) {
            return cursor.query(new JSONPathEquals(args.get(2), args.get(3)));
        } else if (args.get(1).equalsIgnoreCase("jsonpath_oneof")) {
            return cursor.query(new JSONPathOneOf(args.get(2), args.subList(3, args.size() - 1)));
        } else if (args.get(1).equalsIgnoreCase("jsonpath_atleastone")) {
            return cursor.query(new JSONPathAtLeastOne(args.get(2)));
        }

        return cursor;
    }
}
