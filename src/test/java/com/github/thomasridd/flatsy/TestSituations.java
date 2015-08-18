package com.github.thomasridd.flatsy;

import java.nio.file.Paths;

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
}
