package com.github.thomasridd.flatsy.utils;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.operations.operators.Migrate;
import com.github.thomasridd.flatsy.operations.operators.UriToOutput;
import com.github.thomasridd.flatsy.query.matchers.JSONPathExists;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyUtils {
    public boolean isValidJSON(FlatsyObject object) {
        return true;
    }
    public <T> Object tryDeserialise(FlatsyObject object, Class<T> tClass) {
        return null;
    }

    public static void copyCSDBdatasetsToSomewhereElse() {
        Path root = Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master");
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        Path root2 = Paths.get("/Users/thomasridd/Documents/onswebsite/csdb");
        FlatsyDatabase db2 = new FlatsyFlatFileDatabase(root2);

        db.root().query("{uri_contains:datasets}").query("{uri_ends:.json}").query("{is_file}").query(new JSONPathExists("$.downloads[0].cdids[0]")).apply(new Migrate(db2));
        db.root().query("{uri_contains:datasets}").query("{uri_ends:.csdb}").query("{is_file}").apply(new Migrate(db2));
    }


}
