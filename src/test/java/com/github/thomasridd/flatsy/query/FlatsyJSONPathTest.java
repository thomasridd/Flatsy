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
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * JSONPath Tests
 */
public class FlatsyJSONPathTest {
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
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/backup/content_live/zebedee/master"));

        // When
        // we create a cursor
        FlatsyMatcher jsonPath = new JSONPathEquals("$.description.contact.name","Bhavik Patel");
        FlatsyCursor cursor = db.root().query("block:{uri_contains:timeseries}").query("{uri_ends:data.json}").query(jsonPath);

        // Then
        // we expect something non null
        while(cursor.next()) {
            System.out.println(cursor.currentObject().uri);
        }
    }

    @Test
    public void jsonPathReturnsOne_inBigDataset_shouldFindExpectedResult() {
        // Given
        // a database system and a simple query
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/backup/content_live/zebedee/master"));

        // When
        // we create a cursor
        FlatsyMatcher jsonPath = new JSONPathReturnsOne("$.description.keywords[?(@ == gdp)]");
        FlatsyCursor cursor = db.root().query("block:{uri_contains:timeseries}").query("{uri_ends:data.json}").query(jsonPath);

        // Then
        // we expect something non null
        while(cursor.next()) {
            System.out.println(cursor.currentObject().uri);
        }
    }
}