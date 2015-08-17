package com.github.thomasridd.flatsy;

import java.nio.file.Paths;

import com.github.thomasridd.flatsy.query.FlatsyCursor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by thomasridd on 17/08/15.
 */
public class TestSituations {

    @Test
    public void iterator_withRealDatabase_worksThroughData() {

        //FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master"));
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/Tom.Ridd/Documents/onswebsite/zebedee/master"));

        FlatsyCursor cursor = db.rootObject().query("blacklist:{uri_contains:timeseries}").query("{uri_ends:data.json}");

        int counter = 0;

        while(cursor.next() && counter < 1000000) {
            if (counter % 10000 == 0) {
                System.out.println(cursor.currentObject().uri);
            }
            counter++;
        }

        System.out.println(counter + " nodes visited");
    }

}
