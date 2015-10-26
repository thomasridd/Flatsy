package com.github.thomasridd.flatsy;

import java.nio.file.Path;

/**
 * Created by thomasridd on 23/10/15.
 */
public class Scripts {



    public static FlatsyCommandLine commandLineForDatasets(String root) {
        FlatsyCommandLine cli = new FlatsyCommandLine();
        cli.runCommand("from " + root);
        cli.runCommand("filter uri_ends /datasets");
        cli.runCommand("filter folders");

        return cli;
    }


    public static void main(String[] args) {

    }
}
