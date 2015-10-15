package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.util.FlatsyUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Tom.Ridd on 14/10/15.
 */
public class OperatorCommandLineParser {

    public static void applyFromCommand(FlatsyCursor cursor, String command) {
        List<String> args = FlatsyUtil.commandArguments(command);

        if (args.get(0).equalsIgnoreCase("copy")) {
            // copy objects to a new filesystem
            cursor.apply(new Copy(new FlatsyFlatFileDatabase(Paths.get(args.get(1)))));

        } else if (args.get(0).equalsIgnoreCase("replace")) {
            // replace text in each object
            cursor.apply(new Replace(args.get(1), args.get(2)));

        } else if (args.get(0).equalsIgnoreCase("list")) {
            // list all matching objects
            if (args.size() > 1) {
                try (OutputStream stream = Files.newOutputStream(Paths.get(args.get(1)))) {
                    cursor.apply(new UriToOutput(stream));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                cursor.apply(new UriToOutput(System.out));
            }
        } else if (args.get(0).equalsIgnoreCase("table")) {

            List<String> paths = args.subList(1, args.size());

            // list all matching objects
            if (paths.get(0).startsWith("$") == false) {
                try (OutputStream stream = Files.newOutputStream(Paths.get(paths.get(0)))) {
                    cursor.apply(new JSONPathsToOutput(stream, paths.subList(1, paths.size())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                cursor.apply(new JSONPathsToOutput(System.out, paths));
            }
        }

    }
}
