package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.operations.operators.JSONPathsToOutput;
import com.github.thomasridd.flatsy.operations.operators.Migrate;
import com.github.thomasridd.flatsy.operations.operators.UriToOutput;
import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.query.matchers.JSONPathEquals;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasridd on 17/08/15.
 */
public class TestSituations {

    @Test
    public void iterator_withRealDatabase_worksThroughData() {
    long start = System.currentTimeMillis();

        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/backup/content_live/zebedee/master"));

        FlatsyCursor cursor = db.root().query("block:{uri_contains:timeseries}").query("{uri_ends:data.json}").query("{content_contains:clegg}");

        int counter = 0;

        while(cursor.next() && counter < 1000000) {
            //if (counter % 10000 == 0) {
                System.out.println(cursor.currentObject().uri);
            //}
            counter++;
        }

        System.out.println(counter + " nodes identified in " + (System.currentTimeMillis() - start) + " milliseconds");

    }

    @Test
    public void iterator_withRealDatabase_identifiesUris() {
        long start = System.currentTimeMillis();
        String uri = "economy/inflationandpriceindices/timeseries/d7g7";

        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/backup/content_live/zebedee/master"));

        FlatsyCursor cursor = db.root()
                .query("block:{uri_contains:timeseries}")
                .query("{uri_ends:data.json}")
                .query("{content_contains:" + uri + "}");

        int counter = 0;

        while(cursor.next() && counter < 1000000) {
            //if (counter % 10000 == 0) {
            System.out.println(cursor.currentObject().uri);
            //}
            counter++;
        }

        System.out.println(counter + " nodes identified in " + (System.currentTimeMillis() - start) + " milliseconds");

    }

    @Test
    public void iterator_withRealDatabase_runsMigrateUpdates() {
        long start = System.currentTimeMillis();

        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/backup/content_live/zebedee/master"));
        FlatsyDatabase db2 = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/flatsy"));
        //FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/Tom.Ridd/Documents/onswebsite/zebedee/master"));

        // Query should apply a migrate operator to every identified object
        db.root().query("{uri_contains:bulletins}")
                .query("{uri_ends:data.json}")
                .apply(new Migrate(db2));


        System.out.println("Migrated in " + (System.currentTimeMillis() - start) + " milliseconds");

    }

    @Test
    public void jsonPathsToOutputOperator_forQuery_shouldListObjects() throws IOException {
        // Given
        // a database with a text file
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master"));

        // When
        // we run migrate to a second database
        String results = null;
        List<String> paths = new ArrayList<>();
        paths.add("$.uri");
        paths.add("$.description.title");
        paths.add("$.description.edition");
        paths.add("$.description.contact.name");
        paths.add("$.type");

        try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            db.root().query("block:{uri_contains:timeseries}").query("{is_file}").query("{uri_ends:data.json}").apply(new JSONPathsToOutput(stream, paths));
            results = new String(stream.toByteArray(), "UTF8");
        }


        // Then
        // we expect the fi in the new database
        System.out.println(results);
    }

    @Test
    public void jsonMatcher_givenDatabase_canPickoutJson() {
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master"));

        JSONPathEquals matcher = new JSONPathEquals("$.description.edition", "Yes");
        db.root().query("block:{uri_contains:timeseries}").query("{is_file}").query("{uri_ends:data.json}").query(matcher).apply(new UriToOutput(System.out));
    }
}
