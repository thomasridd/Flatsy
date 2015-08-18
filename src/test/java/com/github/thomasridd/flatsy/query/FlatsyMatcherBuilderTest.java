package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.Builder;
import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;

import static org.junit.Assert.*;

public class FlatsyMatcherBuilderTest {
    Path root = null;

    @Before
    public void setUp() throws Exception {
        root = Builder.cursorTestDatabase();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(root.toFile());
    }

    @Test
    public void startsWithQuery_createdWithInterpreter_shouldReturnExpectedResults() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root()).query("{uri_starts:beta}");

        // Then
        // we expect something non null
        cursor.next();
        assertEquals("beta", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("beta/four.json", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("beta/three.json", cursor.currentObject().uri);
        assertFalse(cursor.next());

    }

    @Test
    public void endsWithQuery_createdWithInterpreter_shouldReturnExpectedResults() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        db.create(new FlatsyObject("test/data/data/data.razzle", db), "test item");

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root()).query("{uri_ends:.razzle}");

        // Then
        // we expect something non null
        assertTrue(cursor.next());
        assertEquals("test/data/data/data.razzle", cursor.currentObject().uri);
        assertFalse(cursor.next());
    }

    @Test
    public void containsQuery_createdWithInterpreter_shouldReturnExpectedResults() {
        // Given
        // a database system with a couple of bonus items
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        db.create(new FlatsyObject("test/one/data.json", db), "test item");

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root()).query("{uri_contains:one}");

        // Then
        // we expect something non null
        assertTrue(cursor.next());
        assertEquals("alpha/one.json", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("test/one", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("test/one/data.json", cursor.currentObject().uri);
        assertFalse(cursor.next());
    }

    @Test
    public void isFileQuery_createdWithInterpreter_shouldReturnFiles() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root()).query("{isfile}");

        // Then
        // we expect something non null
        assertTrue(cursor.next());
        assertEquals("alpha/one.json", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("alpha/two.json", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("beta/four.json", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("beta/three.json", cursor.currentObject().uri);
        assertFalse(cursor.next());
    }

    @Test
    public void isFolderQuery_createdWithInterpreter_shouldReturnFolders() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root()).query("{isfolder}");

        // Then
        // we expect something non null
        assertTrue(cursor.next());
        assertEquals("alpha", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("beta", cursor.currentObject().uri);
        assertFalse(cursor.next());
    }

    @Test
    public void blacklistQuery_createdWithInterpreter_shouldNotStopOnBlacklistedNodes() {
        // Given
        // a database system with a couple of bonus items
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        db.create(new FlatsyObject("test/alpha/data.json", db), "test item");

        // When
        // we create a cursor
        FlatsyCursor cursor = db.root().query("block:{uri_contains:alpha}");

        // Then
        // we expect something non null
        assertTrue(cursor.next());
        assertEquals("beta", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("beta/four.json", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("beta/three.json", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("test", cursor.currentObject().uri);
        assertFalse(cursor.next());
    }

    @Test
    public void stopOnMatchQuery_createdWithInterpreter_shouldGiveMinimalNodes() {
        // Given
        // a database system with a couple of bonus items
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        db.create(new FlatsyObject("test/alpha/data.json", db), "test item");

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root()).query("stop:{uri_contains:alpha}");

        // Then
        // we expect something non null
        assertTrue(cursor.next());
        assertEquals("alpha", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("test/alpha", cursor.currentObject().uri);
        assertFalse(cursor.next());
    }

    @Test
    public void multipleQuery_createdWithInterpreter_shouldBringBackDoubleFilteredResults() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        db.create(new FlatsyObject("beta/test/four.json", db), "test content");


        // When
        // we create a cursor
        FlatsyCursor cursor = db.root().query("block:{uri_starts:Beta}").query("{uri_contains:four}");

        // Then
        // we expect only the files
        cursor.next();
        assertEquals("beta/four.json", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("beta/test/four.json", cursor.currentObject().uri);
        assertFalse(cursor.next());

    }

}