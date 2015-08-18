package com.github.thomasridd.flatsy;

import java.nio.file.Paths;

import com.github.thomasridd.flatsy.operations.operators.Migrate;
import com.github.thomasridd.flatsy.query.FlatsyCursor;
import org.junit.Test;

/**
 * Created by thomasridd on 17/08/15.
 */
public class TestSituations {

    @Test
    public void iterator_withRealDatabase_worksThroughData() {
    long start = System.currentTimeMillis();

        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/backup/content_live/zebedee/master"));
        //FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/Tom.Ridd/Documents/onswebsite/zebedee/master"));

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
        //FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/Tom.Ridd/Documents/onswebsite/zebedee/master"));

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
}
