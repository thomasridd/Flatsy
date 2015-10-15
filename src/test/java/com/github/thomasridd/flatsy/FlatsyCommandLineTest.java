package com.github.thomasridd.flatsy;


import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import static org.junit.Assert.*;

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

    @Test
    public void builder_givenSimpleTestScript_shouldProduceOutput() throws IOException {
        // Given
        // a script and a command line to run it
        Path script = Builder.testScript("simplelist.flatsy", simple);

        // When
        // we read the results of the script
        String results = getScriptOutput(script);

        // Then
        // content should not be blank
        assertNotEquals("", results);
    }

    @Test
    public void builder_givenCopyScript_shouldProduceOutput() throws IOException {
        // Given
        // the script
        Path script = Builder.testScript("copyfiles.flatsy", simple);
        Path empty = Builder.emptyTestDatabase();
        Builder.replacePlaceholders(script, "<ROOT2>", empty.toString());

        // When
        // we read the results of the script
        String results = getScriptOutput(script);

        // Then
        // content should not be blank
        assertNotEquals("", results);
    }


    private String getScriptOutput(Path script) throws IOException {
        String content = "";
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PrintStream ps = new PrintStream(baos);
            FlatsyCommandLine cli = new FlatsyCommandLine();
            cli.defaultOut = ps;

            // When
            // we read the results of the script to  it
            cli.runScript(script);

            content = baos.toString("UTF8");
        }
        return content;
    }
}