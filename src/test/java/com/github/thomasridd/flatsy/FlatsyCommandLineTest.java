package com.github.thomasridd.flatsy;


import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.Assert.*;

public class FlatsyCommandLineTest {
    Path root = null;
    Path simple = null;
    Path script = null;

    @Before
    public void setUp() throws Exception {
        // For all tests we copy the flatFileTest example dataset

        root = Builder.copyFlatFiles();
        simple = Builder.cursorTestDatabase();

        // creating script as a before variable
        // and so we can easily rip down
        script = Files.createTempFile("script", "flatsy");
    }

    @After
    public void tearDown() throws Exception {
        // Garbage collection (try to avoid choking the file system)
        FileUtils.deleteDirectory(simple.toFile());
        FileUtils.deleteDirectory(root.toFile());
        Files.delete(script);
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
        cli.runCommand("FILTER jsonpath $.type equals article");
        cli.runCommand("TABLE $.type $.description.title");
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


    // Operations

    @Test
    public void withOperator_givenUri_limitsOperationToFile() throws IOException {
        // Given
        // a script that implements the operation
        try(PrintWriter writer = new PrintWriter(script.toFile(), "UTF-8")) {
            writer.println("from " + root);
            writer.println("with births/data.json list");
        }

        // When
        // we run the script
        String result = getScriptOutput(script);

        // Then
        // we expect sensible output
        assertEquals("births/data.json\n", result);
    }

    @Test
    public void jsonPut_givenJsonObject_putsObject() throws IOException {
        // Given
        // a script that implements a complex operation
        try(PrintWriter writer = new PrintWriter(script.toFile(), "UTF-8")) {
            writer.println("from " + root);
            writer.println("with births/data.json json $ put object {\"field\":\"value\"}");
            writer.println("with births/data.json table $.object.field");
        }

        // When
        // we run the script
        String result = getScriptOutput(script);

        // Then
        // we expect sensible output
        assertEquals("births/data.json\tvalue", result.trim());
    }
}