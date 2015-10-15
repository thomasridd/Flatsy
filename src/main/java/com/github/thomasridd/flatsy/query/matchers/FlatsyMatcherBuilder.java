package com.github.thomasridd.flatsy.query.matchers;

/**
 * Created by Tom.Ridd on 16/08/15.
 */
public class FlatsyMatcherBuilder {

    /**
     * tries to parse a query string to a FlatsyMatcher object
     * @param query a string of the form {<MatcherType>:<Arguments>}
     * @return
     */
    public static FlatsyMatcher queryStringToMatcher(String query) {
        String cleanedString = query.trim();

        String queryType = null;
        String arguments = null;
        if (cleanedString.startsWith("{") && cleanedString.endsWith("}") && cleanedString.contains(":")) {
            cleanedString = cleanedString.substring(1, cleanedString.length() - 1);
            queryType = cleanedString.substring(0, cleanedString.indexOf(":"));
            arguments = cleanedString.substring(cleanedString.indexOf(":") + 1);
        } else if (cleanedString.startsWith("{") && cleanedString.endsWith("}")) {
            queryType = cleanedString.substring(1, cleanedString.length() - 1).trim();
        } else {
            System.out.println("Format for query engine is {<Query Type>:<Arguments>}");
            System.out.println("Query types include, uri_begins, uri_ends, uri_contains, content_contains, is_file, is_folder");
            return null;
        }

        FlatsyMatcher flatsyMatcher = null;
        if (queryType.equalsIgnoreCase("uri_begins")) {
            flatsyMatcher = new UriStartsWith(arguments);
        } else if (queryType.equalsIgnoreCase("uri_ends")) {
            flatsyMatcher = new UriEndsWith(arguments);
        } else if (queryType.equalsIgnoreCase("uri_contains")) {
            flatsyMatcher = new UriContains(arguments);
        } else if (queryType.equalsIgnoreCase("content_contains")) {
            flatsyMatcher = new Find(arguments);
        } else if (queryType.equalsIgnoreCase("is_file")) {
            flatsyMatcher = new IsFile();
        } else if (queryType.equalsIgnoreCase("all")) {
            flatsyMatcher = new All();
        } else if (queryType.equalsIgnoreCase("is_folder")) {
            flatsyMatcher = new IsFolder();
        } else if (queryType.equalsIgnoreCase("json_equals")) {
            String[] args = arguments.split("=");
            flatsyMatcher = new JSONPathEquals(args[0], args[1]);
        }  else {
            return null;
        }

        return flatsyMatcher;
    }

}
