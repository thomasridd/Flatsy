package com.github.thomasridd.flatsy.ons;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.ons.json.CollectionDescription;
import com.github.thomasridd.flatsy.operations.operators.*;
import com.github.thomasridd.flatsy.query.matchers.All;
import com.github.thomasridd.flatsy.query.matchers.JSONPathExists;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by thomasridd on 10/09/15.
 */
public class scripts {

    public static void publishAllData() throws IOException {
        ZebedeeHost.initialiseZebedeeConnection();
        CollectionDescription publishAll = ZebedeeHost.publishedCollection("publishAll");

        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master"));

        long start = System.currentTimeMillis();

        // Upload
        db.root().query("{uri_contains:datasets}").
                query("{uri_ends:.json}").
                query("{is_file}").
                query(new JSONPathExists("$.downloads[0].cdids[0]")).
                apply(new ZebedeeUpload(publishAll));
        db.root().query("{uri_contains:datasets}").
                query("{uri_ends:.csdb}").
                query("{is_file}").
                apply(new ZebedeeUpload(publishAll));


        // Complete
        db.root().query("{uri_contains:datasets}").
                query("{uri_ends:.json}").
                query("{is_file}").
                query(new JSONPathExists("$.downloads[0].cdids[0]")).
                apply(new ZebedeeComplete(publishAll));
        db.root().query("{uri_contains:datasets}").
                query("{uri_ends:.csdb}").
                query("{is_file}").
                apply(new ZebedeeComplete(publishAll));

        // Review
        db.root().query("{uri_contains:datasets}").
                query("{uri_ends:.json}").
                query("{is_file}").
                query(new JSONPathExists("$.downloads[0].cdids[0]")).
                apply(new ZebedeeReview(publishAll));
        db.root().query("{uri_contains:datasets}").
                query("{uri_ends:.csdb}").
                query("{is_file}").
                apply(new ZebedeeReview(publishAll));

        // Review
        ZebedeeHost.approve(publishAll.id);

        System.out.println(System.currentTimeMillis() - start + "ms in total");
    }

    public static void transferCollectionToMaster (){
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master"));
        FlatsyDatabase db2 = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/processed"));
        // Review
        db2.root().query("{uri_contains:/datasets/}").apply(new Migrate(db));
    }

    public static void renameTimeSeriesDatasets() {
        FlatsyDatabase db2 = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/processed"));
        db2.root().query("{uri_contains:datasets}").
                query("{uri_ends:.json}").
                query("{is_file}").
                query(new JSONPathExists("$.downloads[0].cdids[0]")).
                apply(new ReplaceIn("\"type\": \"dataset\"", "\"type\": \"timeseries_dataset\""));
    }

    public static void main(String[] args) throws IOException {
//        publishAllData();
        //renameTimeSeriesDatasets();
        transferCollectionToMaster();
    }
}
