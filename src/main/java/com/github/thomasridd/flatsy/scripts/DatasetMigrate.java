package com.github.thomasridd.flatsy.scripts;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.query.FlatsyCursor;

import java.nio.file.Paths;

/**
 * Created by thomasridd on 29/09/15.
 */
public class DatasetMigrate {
    public static void findAllDatasets() {
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master"));

        db.root().query("{uri_contains:datasets}").query("{uri_contains:data.json}").apply("{json_paths_to_console:[\"$.type\"]}");
    }

    public static void main(String[] args) {
        DatasetMigrate.findAllDatasets();
    }
}
