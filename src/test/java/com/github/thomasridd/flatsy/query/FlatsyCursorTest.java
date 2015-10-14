package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.Builder;
import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.query.matchers.*;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;

import static org.junit.Assert.*;

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
        FlatsyQuery query = new FlatsyQuery(new UriStartsWith("Beta"));

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root(), query);

        // Then
        // we expect something non null
        assertNotNull(cursor);
    }

    @Test
    public void next_givenQueryWithResults_shouldReturnTrue() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        FlatsyQuery query = new FlatsyQuery(new UriStartsWith("Beta"));

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root(), query);

        // Then
        // we expect something non null
        assertNotNull(cursor);
        assertTrue(cursor.next());
    }

    @Test
    public void next_givenQueryWithNoResults_shouldReturnFalse() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        FlatsyQuery query = new FlatsyQuery(new UriStartsWith("No results"));

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root(), query);

        // Then
        // we expect something non null
        assertNotNull(cursor);
        assertFalse(cursor.next());
    }

    @Test
    public void startsWithQuery_givenDatabase_shouldReturnExpectedResults() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        FlatsyQuery query = new FlatsyQuery(new UriStartsWith("Beta"));

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root(), query);

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
        FlatsyQuery query = new FlatsyQuery(new UriEndsWith("razzle"));

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root(), query);

        // Then
        // we expect something non null
        assertTrue(cursor.next());
        assertEquals("test/data/data/data.razzle", cursor.currentObject().uri);
        assertFalse(cursor.next());
    }

    @Test
    public void containsQuery_givenDatabase_shouldReturnExpectedResults() {
        // Given
        // a database system with a couple of bonus items
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        db.create(new FlatsyObject("test/one/data.json", db), "test item");
        FlatsyQuery query = new FlatsyQuery(new UriContains("one"));

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root(), query);

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
    public void isFileQuery_givenDatabase_shouldReturnFiles() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        FlatsyQuery query = new FlatsyQuery(new IsFile());

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root(), query);

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
    public void isFolderQuery_givenDatabase_shouldReturnFolders() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        FlatsyQuery query = new FlatsyQuery(new IsFolder());

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root(), query);

        // Then
        // we expect something non null
        assertTrue(cursor.next());
        assertEquals("", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("alpha", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("beta", cursor.currentObject().uri);
        assertFalse(cursor.next());
    }

    @Test
    public void blockerQuery_givenDatabase_shouldNotStopOnBlacklistedNodes() {
        // Given
        // a database system with a couple of bonus items
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        db.create(new FlatsyObject("test/alpha/data.json", db), "test item");
        FlatsyQuery query = new FlatsyQuery(FlatsyQueryType.Blocker, new UriContains("alpha"));

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root(), query);

        // Then
        // we expect something non null
        assertTrue(cursor.next());
        assertEquals("", cursor.currentObject().uri);
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
    public void haltQuery_givenDatabase_shouldGiveMinimalNodes() {
        // Given
        // a database system with a couple of bonus items
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        FlatsyQuery query = new FlatsyQuery(FlatsyQueryType.ConditionBlocker, new UriContains("alpha"));

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root(), query);

        // Then
        // we expect something non null
        assertTrue(cursor.next());
        assertEquals("", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("alpha", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("beta", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("beta/four.json", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("beta/three.json", cursor.currentObject().uri);
        assertFalse(cursor.next());
    }

    @Test
    public void multipleQuery_givenDatabase_shouldBringBackDoubleFilteredResults() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        db.create(new FlatsyObject("beta/test/four.json", db), "test content");

        FlatsyMatcher startsWith = new UriStartsWith("Beta");
        FlatsyMatcher contains = new UriContains("four");


        // When
        // we create a cursor
        FlatsyCursor cursor = db.root().query(startsWith).query(contains);

        // Then
        // we expect only the files
        cursor.next();
        assertEquals("beta/four.json", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("beta/test/four.json", cursor.currentObject().uri);
        assertFalse(cursor.next());

    }

    @Test
    public void contentContainsQuery_givenDatabase_shouldReturnFiles() {
        // Given
        // a database system and a simple query (that should bring results)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        db.create(new FlatsyObject("alpha/contains_word.json", db), "I contain the word camel");
        db.create(new FlatsyObject("test/contains_word.json", db), "My camel is called Louise");

        FlatsyQuery query = new FlatsyQuery(new ContentContains("camel"));

        // When
        // we create a cursor
        FlatsyCursor cursor = new FlatsyCursor(db.root(), query);

        // Then
        // we expect something non null
        assertTrue(cursor.next());
        assertEquals("alpha/contains_word.json", cursor.currentObject().uri);
        assertTrue(cursor.next());
        assertEquals("test/contains_word.json", cursor.currentObject().uri);
        assertFalse(cursor.next());
    }
}