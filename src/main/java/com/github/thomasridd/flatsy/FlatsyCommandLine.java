package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.operations.operators.FlatsyOperator;
import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.query.matchers.MatcherCommandLineParser;
import com.github.thomasridd.flatsy.util.FlatsyUtil;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasridd on 14/10/15.
 * <p/>
 * Syntax options
 * <p/>
 * SELECT [path to root of FlatsyFlatFileDatabase]
 * <p/>
 * FILTER
 * <p/>
 * COPY
 * REPLACE
 * URI_LIST [File path to write list (optional)]
 * FILE_LIST [File path to write list (optional)]
 */
public class FlatsyCommandLine {
    FlatsyDatabase db;
    List<String> queryCommands = new ArrayList<>();

    /**
     * Build a command using flatsy command line syntax
     *
     * @param command a Flatsy command
     * @return true if complete
     */
    public boolean runCommand(String command) {
        // get the list of arguments
        List<String> args = FlatsyUtil.commandArguments(command);

        String action = args.get(0).trim();
        if (action.equalsIgnoreCase("select")) {

            // set the database root
            db = new FlatsyFlatFileDatabase(Paths.get(args.get(1).trim()));
            queryCommands = new ArrayList<>();
            return true;
        } else if (action.equalsIgnoreCase("filter")) {

            // add to the query
            queryCommands.add(command);
            return true;
        } else {

            // run a command
            return applyOperation(args);
        }

    }

    protected boolean applyOperation(List<String> args) {
        // Generate the cursor for the file system
        FlatsyCursor query = buildQuery();
        if (query == null) return false;

        // Generate the operator
        FlatsyOperator operation = buildOperation(args);
        if (operation == null) return false;

        // Apply the operation
        query.apply(operation);

        return true;
    }

    protected FlatsyCursor buildQuery() {
        return MatcherCommandLineParser.cursorFromFilterCommands(db, queryCommands);
    }

    protected FlatsyOperator buildOperation(List<String> args) {

        return null;
    }

}
