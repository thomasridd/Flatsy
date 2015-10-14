package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.*;
import com.github.thomasridd.flatsy.operations.FlatsyWorker;
import com.github.thomasridd.flatsy.query.FlatsyCursor;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by thomasridd on 18/08/15.
 */
public class FlatsyOperatorTests {
    Path root = null;
    Path emptyRoot = null;

    @Before
    public void setUp() throws Exception {
        root = Builder.cursorTestDatabase();
        emptyRoot = Files.createTempDirectory("emptyRoot");
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(root.toFile());
        FileUtils.deleteDirectory(emptyRoot.toFile());
    }

    @Test
    public void replaceInOperator_forSingleObject_shouldReplaceText() throws IOException {
        // Given
        // a database with a text file
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        FlatsyObject object = new FlatsyObject("test/camel.txt", db);
        object.create("Alice was the name of my camel");

        // When
        // we run replace on the text file
        FlatsyOperator operator = new ReplaceIn("camel", "sister");
        operator.apply(object);

        // Then
        // we expect
        String replaced = object.retrieve();
        assertEquals("Alice was the name of my sister", replaced);
    }
    @Test
    public void replaceInOperator_forMultipleObjects_shouldReplaceText() throws IOException {
        // Given
        // a database with a text file
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        FlatsyObject object = new FlatsyObject("test/camel.txt", db);
        object.create("Alice was the name of my camel");

        // When
        // we run replace on the text file
        FlatsyOperator operator = new ReplaceIn("camel", "sister");
        FlatsyWorker worker = new FlatsyWorker(operator);

        FlatsyCursor cursor = db.root().query("{is_file}");
        worker.updateAll(cursor);

        // Then
        // we expect
        String replaced = object.retrieve();
        assertEquals("Alice was the name of my sister", replaced);
    }
    @Test
    public void migrateOperator_forMultipleObjects_shouldCopyObjects() throws IOException {
        // Given
        // a database with a text file
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        FlatsyObject object1 = new FlatsyObject("test/camel.txt", db);
        FlatsyObject object2 = new FlatsyObject("camel/test.txt", db);
        object1.create("Alice was the name of my camel");
        object2.create("My camel was called Alice");

        // When
        // we run migrate to a second database
        FlatsyDatabase target = new FlatsyFlatFileDatabase(emptyRoot);
        FlatsyOperator operator = new Migrate(target);
        db.root().query("{uri_contains:camel}").apply(operator);

        // Then
        // we expect copies of the files in the new database
        FlatsyObject clone1 = new FlatsyObject("test/camel.txt", target);
        FlatsyObject clone2 = new FlatsyObject("camel/test.txt", target);
        assertEquals(FlatsyObjectType.OtherFile, clone1.getType());
        assertEquals(FlatsyObjectType.OtherFile, clone2.getType());

        assertEquals(object1.retrieve(), clone1.retrieve());
        assertEquals(object2.retrieve(), clone2.retrieve());

    }

    @Test
         public void uriToOutputOperator_forQuery_shouldListObjects() throws IOException {
        // Given
        // a database with a text file
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        // When
        // we run migrate to a second database
        String results = null;
        try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            db.root().apply(new UriToOutput(stream));
            results = new String(stream.toByteArray(), "UTF8");
        }


        // Then
        // we expect the fi in the new database
        assertTrue(results.startsWith("\nalpha\nalpha/one.json\nalpha/two.json"));
    }


}