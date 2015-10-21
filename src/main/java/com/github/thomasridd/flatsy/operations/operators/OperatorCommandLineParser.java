package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.util.FlatsyUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Tom.Ridd on 14/10/15.
 */
public class OperatorCommandLineParser {

    public static void applyFromCommand(FlatsyCursor cursor, String command) {
        applyFromCommand(cursor, command, System.out);
    }
    public static void applyFromCommand(FlatsyCursor cursor, String command, OutputStream defaultOut) {
        List<String> args = FlatsyUtil.commandArguments(command);

        String action = args.get(0);
        if (action.equalsIgnoreCase("copy")) {
            // copy objects to a new filesystem
            cursor.apply(new Copy(new FlatsyFlatFileDatabase(Paths.get(args.get(1)))));

        } else if (action.equalsIgnoreCase("copy_to")) {
            // replace text in each object
            cursor.apply(new CopyTo(new FlatsyFlatFileDatabase(Paths.get(args.get(1))), args.get(2)));

        } else if (action.equalsIgnoreCase("folder_copy")) {
            // replace text in each object
            cursor.apply(new Copy(new FlatsyFlatFileDatabase(Paths.get(args.get(1))), true));

        } else if (action.equalsIgnoreCase("folder_copy_to")) {
            // replace text in each object
            cursor.apply(new CopyTo(new FlatsyFlatFileDatabase(Paths.get(args.get(1))), args.get(2), true));

        } else if (action.equalsIgnoreCase("replace")) {
            // replace text in each object
            cursor.apply(new Replace(args.get(1), args.get(2)));

        } else if (action.equalsIgnoreCase("list")) {
            // list all matching objects
            if (args.size() > 1) {
                try (OutputStream stream = Files.newOutputStream(Paths.get(args.get(1)))) {
                    cursor.apply(new UriToOutput(stream));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                cursor.apply(new UriToOutput(defaultOut));
            }
        } else if (action.equalsIgnoreCase("table")) {

            List<String> paths = args.subList(1, args.size());

            // list all matching objects
            if (paths.get(0).startsWith("$") == false) {
                try (OutputStream stream = Files.newOutputStream(Paths.get(paths.get(0)))) {
                    cursor.apply(new JSONPathsToOutput(stream, paths.subList(1, paths.size())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                cursor.apply(new JSONPathsToOutput(defaultOut, paths));
            }
        } else if (action.equalsIgnoreCase("jsonpath")) {
            String jsonpath = args.get(1);
            String jsonAction = args.get(2);

            if (jsonAction.equalsIgnoreCase("add")) {

            } else if (jsonAction.equalsIgnoreCase("put")) {
                if (args.size() >= 5) {

                    String jsonField = args.get(3);
                    String jsonValue = args.get(4);

                    cursor.apply(new JSONPathPut(jsonpath, jsonField, jsonValue));

                }
            }
        }

    }
}
