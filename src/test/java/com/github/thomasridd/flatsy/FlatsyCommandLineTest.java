package com.github.thomasridd.flatsy;


import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

public class FlatsyCommandLineTest {
    Path root = null;
    Path simple = null;

    @Before
    public void setUp() throws Exception {
        // For all tests we copy the flatFileTest example dataset

        root = Builder.copyFlatFiles();
        simple = Builder.cursorTestDatabase();
    }

    @After
    public void tearDown() throws Exception {
        // Garbage collection (try to avoid choking the file system)
        FileUtils.deleteDirectory(simple.toFile());
        FileUtils.deleteDirectory(root.toFile());
    }

    @Test
    public void flatsyCommandLine_onInitialise_shouldNotBeNull() throws IOException {
        // Given
        // a command line
        FlatsyCommandLine cli = new FlatsyCommandLine();

        // When
        // we add commands
        cli.runCommand("FROM " + root);
        cli.runCommand("FILTER uri_ends data.json");
        cli.runCommand("FILTER jsonpath_equals $.type article");
        cli.runCommand("TABLE $.type $.description.summary");
    }
}