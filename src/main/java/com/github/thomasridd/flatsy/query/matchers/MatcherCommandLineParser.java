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

        FlatsyMatcher matcher = null;
        boolean invert = false;


        if (args.get(1).equalsIgnoreCase("not")) {
            // invert the rest of the filter string
            invert = true;
            args.remove(1);
        }

        String keyword = args.get(1);
        if (keyword.equalsIgnoreCase("files")) {
            // files only
            matcher = new IsFile();

        } else if (keyword.equalsIgnoreCase("folders")) {
            // folders only
            matcher = new IsFolder();

        } else if (keyword.equalsIgnoreCase("uri_contains")) {
            // paths where the uri contains a specific string
            matcher = new UriContains(args.get(2));

        } else if (keyword.equalsIgnoreCase("uri_ends")) {
            // paths where the uri ends with specific string
            matcher = new UriEndsWith(args.get(2));

        } else if (keyword.equalsIgnoreCase("find")) {
            // paths where the uri ends with specific string
            matcher = new Find(args.get(2));

        } else if (keyword.equalsIgnoreCase("jsonpath")) {

            if (args.get(2).startsWith("$")) {

                String jsonPath = args.get(2);
                if (args.get(3).equalsIgnoreCase("equals")) {
                    matcher = new JSONPathOneOf(jsonPath, args.subList(4, args.size()));
                } else if (args.get(3).equalsIgnoreCase("exists")) {
                    matcher = new Or(new JSONPathExists(jsonPath), new JSONPathAtLeastOne(jsonPath));
                }
            } else if (args.get(2).equalsIgnoreCase("valid")) {
                matcher = new JSONValid();
            }
        }

        // Return result if found
        if (matcher != null) {
            if (invert) {
                return cursor.query(new Not(matcher));
            } else {
                return cursor.query(matcher);
            }
        }

        System.out.println("Could not parse " + filter);
        return cursor;
    }
}
