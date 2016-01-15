package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.operations.operators.OperatorCommandLineParser;
import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.query.matchers.MatcherCommandLineParser;
import com.github.thomasridd.flatsy.util.FlatsyUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * See
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
     * @throws IOException if script cannot be opened
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
        if (action.equalsIgnoreCase("from") || action.equalsIgnoreCase("select")) {

            // set the database root
            db = new FlatsyFlatFileDatabase(FlatsyUtil.pathsGet(args.get(1).trim()));
            queryCommands = new ArrayList<>();
            return true;
        } else if (action.equalsIgnoreCase("filter") || action.equalsIgnoreCase("where") || action.equalsIgnoreCase("and")) {

            // add to the query
            queryCommands.add(command);
            return true;
        } else if (action.equalsIgnoreCase("clear")) {

            // clear query commands
            queryCommands = new ArrayList<>();
            return true;
        } else if (action.equalsIgnoreCase("with")) {

            // Apply an operator to a single uri
            if (args.size() >= 3) {
                // get the uri
                String uri = args.get(1).trim();
                // parse the operator
                args = args.subList(2, args.size());
                FlatsyObject object = db.get(uri);

                OperatorCommandLineParser.applyFromCommand(object, args, defaultOut);
                return true;
            }
            return true;

        } else if (action.equalsIgnoreCase("print")) {
            System.out.println("");
            System.out.println("FROM " + db.toString());
            for (String query: queryCommands) {
                System.out.println(query);
            }
            return true;

        } else if (action.equalsIgnoreCase("delete") && args.size() > 1) {
            FlatsyObject object = db.get(args.get(1).trim());
            if (object.getType() != FlatsyObjectType.Null) {
                db.delete(object);
            }
            return true;
        } else {
            long start = System.currentTimeMillis();

            // run a command
            boolean result = applyOperation(command);

            long total = System.currentTimeMillis() - start;

            System.out.println(command + " >> Applied in " +  total + " milliseconds");

            return result;
        }
    }

    /**
     * Run a sequence of script commands
     *
     * @param commands a list of Flatsy query language commands
     * @return success
     */
    public boolean runScript(List<String> commands) {
        boolean result = true;
        for (String command: commands) {
            result = result && runCommand(command);
        }
        return result;
    }

    /**
     * Run a sequence of script commands from file
     *
     * @param path the file to run
     * @return success
     * @throws IOException if script cannot be read
     */
    public boolean runScript(Path path) throws IOException {
        boolean result = true;
        try(Scanner scanner = new Scanner(Files.newInputStream(path))) {
            while (scanner.hasNextLine()) {
                result = result && runCommand(scanner.nextLine());
            }
        }
        return result;
    }

    /**
     * Run a sequence of script commands from stream
     *
     * @param stream the input stream
     * @return success
     */
    public boolean runScript(InputStream stream) {
        boolean result = true;
        Scanner scanner = new Scanner(stream);
        while(scanner.hasNextLine()) {
            result = result && runCommand(scanner.nextLine());
        }
        return result;
    }


    /**
     * Create a cursor from a saved script
     *
     * @param scriptPath the path of the script
     * @return a flatsy cursor
     *
     * @throws IOException if the script cannot be read
     */
    public FlatsyCursor cursor(Path scriptPath) throws IOException {
        db = null;
        try(Scanner scanner = new Scanner(Files.newInputStream(scriptPath))) {
            while (scanner.hasNextLine()) {
                buildCursor(scanner.nextLine());
            }
        }
        return MatcherCommandLineParser.cursorFromFilterCommands(db, queryCommands);
    }

    /**
     * Create a cursor based on a command list
     *
     * @param commands an array of commands to run
     * @return a flatsy cursor
     */
    public FlatsyCursor cursor(List<String> commands) {
        for (String command: commands) {
            buildCursor(command);
        }
        return MatcherCommandLineParser.cursorFromFilterCommands(db, queryCommands);
    }

    /**
     * Create cursor based on currently saved query commands
     *
     * @return a flatsy cursor
     */
    public FlatsyCursor cursor() {
        return MatcherCommandLineParser.cursorFromFilterCommands(db, queryCommands);
    }

    /**
     * Add filters to the list of query commands ignoring any execute statements
     *
     * @param command a Flatsy command
     * @return true if complete
     */
    private boolean buildCursor(String command) {
        // skip out on blank lines
        if (command.trim().length() == 0) return true;

        // get the list of arguments
        List<String> args = FlatsyUtil.commandArguments(command);

        String action = args.get(0).trim();
        if (action.equalsIgnoreCase("select") || action.equalsIgnoreCase("from")) {

            // set the database root
            db = new FlatsyFlatFileDatabase(FlatsyUtil.pathsGet(args.get(1).trim()));
            queryCommands = new ArrayList<>();
            return true;
        } else if (action.equalsIgnoreCase("where") || action.equalsIgnoreCase("and") || action.equalsIgnoreCase("filter")) {

            // add to the query
            queryCommands.add(command);
            return true;
        } else if (action.equalsIgnoreCase("clear")) {
            queryCommands = new ArrayList<>();
            return true;
        }
        return false;
    }

    /**
     * Apply an operation to the cursor currently defined by queryCommands
     *
     * @param command an operation in Flatsy syntax
     * @return success
     */
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

    public static void main(String[] args) {
        FlatsyCommandLine cli = new FlatsyCommandLine();
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.print("flatsy >> ");
            String command = scanner.nextLine();
            cli.runCommand(command);
        }
    }
}
