package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.operations.operators.OperatorCommandLineParser;
import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.query.matchers.MatcherCommandLineParser;
import com.github.thomasridd.flatsy.util.FlatsyUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    public OutputStream defaultOut = System.out; // Convenience for tests

    /**
     * Create a new FlatsyCommandLine
     */
    public FlatsyCommandLine() {};

    /**
     * Create a new FlatsyCommandLine and run a command
     * @param command the command
     */
    public FlatsyCommandLine(String command) {
        runCommand(command);
    }

    /**
     * Create a new FlatsyCommandLine and run a list of commands
     * @param commands a list of commands
     */
    public FlatsyCommandLine(List<String> commands) {
        runScript(commands);
    }

    /**
     * Create a new FlatsyCommandLine and run a script of commands
     * @param script a path to a script
     */
    public FlatsyCommandLine(Path script) throws IOException {
        runScript(script);
    }

    /**
     * Create a new FlatsyCommandLine and run a script of commands
     * @param stream a stream for script commands
     */
    public FlatsyCommandLine(InputStream stream) {
        runScript(stream);
    }

    /**
     * Build a command using flatsy command line syntax
     *
     * @param command a Flatsy command
     * @return true if complete
     */
    public boolean runCommand(String command) {
        // skip out on blank lines
        if (command.trim().length() == 0) return true;

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

    public boolean runScript(List<String> commands) {
        boolean result = true;
        for (String command: commands) {
            result = result && runCommand(command);
        }
        return result;
    }

    public boolean runScript(Path path) throws IOException {
        boolean result = true;
        try(Scanner scanner = new Scanner(Files.newInputStream(path))) {
            while (scanner.hasNextLine()) {
                result = result && runCommand(scanner.nextLine());
            }
        }
        return result;
    }

    public boolean runScript(InputStream stream) {
        boolean result = true;
        Scanner scanner = new Scanner(stream);
        while(scanner.hasNextLine()) {
            result = result && runCommand(scanner.nextLine());
        }
        return result;
    }

    protected boolean applyOperation(String command) {
        // Simply chuck blank lines
        if (command.trim().equalsIgnoreCase("")) { return true;}

        // Generate the cursor for the file systems
        FlatsyCursor query = MatcherCommandLineParser.cursorFromFilterCommands(db, queryCommands);
        if (query == null) return false;

        // Generate the operator
        OperatorCommandLineParser.applyFromCommand(query, command, defaultOut);

        return true;
    }
}
