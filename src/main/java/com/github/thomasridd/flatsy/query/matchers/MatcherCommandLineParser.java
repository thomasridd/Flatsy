package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.util.FlatsyUtil;

import java.util.List;

/**
 * Created by thomasridd on 14/10/15.
 *
 * Simple class to build matcher objects using a query language
 */
public class MatcherCommandLineParser {

    /**
     * Build a Flatsy cursor from a list of filters
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

    /**
     * Build a query from command line arguments and append it to a cursor
     *
     * @param cursor the cursor
     * @param filter the filter string
     * @return
     */
    protected static FlatsyCursor applyFilterToCursor(FlatsyCursor cursor, String filter) {
        List<String> args = FlatsyUtil.commandArguments(filter);

        if (args.get(1).equalsIgnoreCase("not")) {
            // invert the rest of the filter string
            return applyNotFilterToCursor(cursor, filter);

        } else if (args.get(1).equalsIgnoreCase("files")) {
            // files only
            return cursor.query(new IsFile());

        } else if (args.get(1).equalsIgnoreCase("folders")) {
            // folders only
            return cursor.query(new IsFolder());

        } else if (args.get(1).equalsIgnoreCase("uri_contains")) {
            // paths where the uri contains a specific string
            return cursor.query(new UriContains(args.get(2)));

        } else if (args.get(1).equalsIgnoreCase("uri_ends")) {
            // paths where the uri ends with specific string
            return cursor.query(new UriEndsWith(args.get(2)));

        } else if (args.get(1).equalsIgnoreCase("find")) {
            // paths where the uri ends with specific string
            return cursor.query(new Find(args.get(2)));

        } else if (args.get(1).equalsIgnoreCase("jsonpath_exists")) {
            // files where the json file includes a specific node
            return cursor.query(new JSONPathExists(args.get(2)));

        } else if (args.get(1).equalsIgnoreCase("jsonpath_equals")) {
            // filter by a value in the json
            return cursor.query(new JSONPathEquals(args.get(2), args.get(3)));

        } else if (args.get(1).equalsIgnoreCase("jsonpath_oneof")) {
            // filter by a value in the json - include a list of options
            return cursor.query(new JSONPathOneOf(args.get(2), args.subList(3, args.size() - 1)));

        } else if (args.get(1).equalsIgnoreCase("jsonpath_atleastone")) {
            //
            return cursor.query(new JSONPathAtLeastOne(args.get(2)));

        }

        System.out.println("Could not parse " + filter);
        return cursor;
    }

    /**
     * Build a query from command line arguments and append it to a cursor w
     *
     * @param cursor the cursor
     * @param filter the filter string
     * @return
     */
    protected static FlatsyCursor applyNotFilterToCursor(FlatsyCursor cursor, String filter) {
        List<String> args = FlatsyUtil.commandArguments(filter);

        if (args.get(2).equalsIgnoreCase("files")) {
            return cursor.query(new Not(new IsFile()));
        } else if (args.get(2).equalsIgnoreCase("folders")) {
            return cursor.query(new Not(new IsFolder()));
        } else if (args.get(2).equalsIgnoreCase("uri_contains")) {
            return cursor.query(new Not(new UriContains(args.get(3))));
        } else if (args.get(2).equalsIgnoreCase("uri_ends")) {
            return cursor.query(new Not(new UriEndsWith(args.get(3))));
        } else if (args.get(2).equalsIgnoreCase("jsonpath_exists")) {
            return cursor.query(new Not(new JSONPathExists(args.get(3))));
        } else if (args.get(2).equalsIgnoreCase("jsonpath_equals")) {
            return cursor.query(new Not(new JSONPathEquals(args.get(3), args.get(4))));
        } else if (args.get(2).equalsIgnoreCase("jsonpath_oneof")) {
            return cursor.query(new Not(new JSONPathOneOf(args.get(3), args.subList(4, args.size() - 1))));
        } else if (args.get(2).equalsIgnoreCase("jsonpath_atleastone")) {
            return cursor.query(new Not(new JSONPathAtLeastOne(args.get(3))));
        }

        System.out.println("Could not parse " + filter);
        return cursor;
    }
}
