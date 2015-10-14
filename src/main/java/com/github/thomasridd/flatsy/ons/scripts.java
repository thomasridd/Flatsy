package com.github.thomasridd.flatsy.ons;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.ons.json.CollectionDescription;
import com.github.thomasridd.flatsy.operations.operators.*;
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

        System.out.println("Publishing data");

        // Upload
        System.out.println("...uploading data");
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
        System.out.println("...completing");
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
        System.out.println("...reviewing");
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
        System.out.println("...approving");
        ZebedeeHost.approve(publishAll.id);

        // Publish
        FlatsyDatabase collectionDb = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/collections/" + publishAll.name + "/reviewed"));
        collectionDb.root().query("{all}").apply(new Migrate(db));

        System.out.println(System.currentTimeMillis() - start + "ms in total");
    }

    public static void transferCollectionToMaster (){
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master"));
        FlatsyDatabase db2 = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/collections/flatsy_publishall_monoct12160437bst2015/reviewed"));
        // Review
        db2.root().query("{all}").apply(new Migrate(db));
    }

    public static void main(String[] args) throws IOException {
//        publishAllData();
        //renameTimeSeriesDatasets();
        transferCollectionToMaster();
    }
}
