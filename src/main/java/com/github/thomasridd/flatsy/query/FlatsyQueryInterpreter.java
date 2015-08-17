package com.github.thomasridd.flatsy.query;

/**
 * Created by Tom.Ridd on 16/08/15.
 */
public class FlatsyQueryInterpreter {

    public static FlatsyQuery queryStringToFlatsyQuery(String query) {
        boolean blacklist = false;
        boolean stopAtFound = false;
        String cleanedString = query;

        if (cleanedString.trim().startsWith("blacklist:")) {
            blacklist = true;
            cleanedString = cleanedString.substring("blacklist:".length());
        } else if (cleanedString.trim().startsWith("stop:")) {
            stopAtFound = true;
            cleanedString = cleanedString.substring("stop:".length());
        }

        String queryType = null;
        String arguments = null;
       if (cleanedString.startsWith("{") && cleanedString.endsWith("}") && cleanedString.contains(":")) {
           cleanedString = cleanedString.substring(1, cleanedString.length() - 2);
           queryType = cleanedString.substring(0, cleanedString.indexOf(":") - 1);
           arguments = cleanedString.substring(cleanedString.indexOf(":") + 1);
       } else if (cleanedString.startsWith("{") && cleanedString.endsWith("}")) {
            queryType = cleanedString.substring(1, cleanedString.length() - 2).trim();
        } else {
            System.out.println("Format for query engine is [blacklist][stop]{<Query Type>:<Arguments>} ");
            System.out.println("Query types include, uri_begins, uri_ends, uri_contains, is_file, is_folder");
            return null;
        }


        FlatsyQuery returnQuery = null;
        if (queryType.equalsIgnoreCase("uri_begins")) {
            returnQuery = new FlatsyQueryUriStartsWith(arguments);
        } else if (queryType.equalsIgnoreCase("uri_ends")) {
            returnQuery = new FlatsyQueryUriEndsWith(arguments);
        } else if (queryType.equalsIgnoreCase("is_file")) {
            returnQuery = new FlatsyQueryIsFile();
        } else if (queryType.equalsIgnoreCase("is_folder")) {
            returnQuery = new FlatsyQueryIsFolder();
        } else {
            return null;
        }

        returnQuery.setBlackLister(blacklist);
        returnQuery.setStopOnMatch(stopAtFound);
        return returnQuery;
    }


}
