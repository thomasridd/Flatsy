package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.operations.operators.OperatorCommandLineParser;
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
 * FROM [path to root of FlatsyFlatFileDatabase]
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

    public static void main(String[] args) {
        FlatsyCommandLine cli = new FlatsyCommandLine();
    }

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
        if (action.equalsIgnoreCase("from")) {

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
            return applyOperation(command);
        }
    }

    protected boolean applyOperation(String command) {
        // Generate the cursor for the file system
        FlatsyCursor query = MatcherCommandLineParser.cursorFromFilterCommands(db, queryCommands);
        if (query == null) return false;

        // Generate the operator
        OperatorCommandLineParser.applyFromCommand(query, command);


        return true;
    }
}
