package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.Builder;
import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import java.nio.file.Path;

public class FlatsyCursorTest {
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
    public void cursor_givenParameters_shouldInstantiate() {
        // Given
        // a database system and a simple query
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        FlatsyQuery query = new FlatsyUriStartsWith("Beta");

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.rootObject(), query);

        // Then
        // we expect something non null
        assertNotNull(cursor);
    }

    @Test
    public void cursor_givenQueryWithResults_shouldReturnTrue() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        FlatsyQuery query = new FlatsyUriStartsWith("Beta");

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.rootObject(), query);

        // Then
        // we expect something non null
        assertNotNull(cursor);
        assertTrue(cursor.next());
    }

    @Test
    public void cursor_givenQueryWithNoResults_shouldReturnFalse() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        FlatsyQuery query = new FlatsyUriStartsWith("No Results");

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.rootObject(), query);

        // Then
        // we expect something non null
        assertNotNull(cursor);
        assertFalse(cursor.next());
    }

    @Test
    public void cursor_givenQueryWithResults_shouldReturnExpectedResults() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        FlatsyQuery query = new FlatsyUriStartsWith("Beta");

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.rootObject(), query);

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
    public void endsWithQuery_givenDatabase_shouldReturnExpectedResults() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        db.create(new FlatsyObject("test/data/data/data.razzle", db), "test item");
        FlatsyQuery query = new FlatsyUriEndsWith(".razzle");

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.rootObject(), query);

        // Then
        // we expect something non null
        assertTrue(cursor.next());
        assertEquals("test/data/data/data.razzle", cursor.currentObject().uri);
        assertFalse(cursor.next());
    }
}