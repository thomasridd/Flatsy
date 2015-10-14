package com.github.thomasridd.flatsy.scripts;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.ons.scripts;
import com.github.thomasridd.flatsy.operations.operators.ZebedeeTimeSeriesStripper;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by thomasridd on 05/10/15.
 */
public class RecalculateDatapoints {

    public static void main(String[] args) throws IOException {

        // Remove all the timeseries datapoints (retain metadata)
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master"));

        db.root().query("{uri_contains:/timeseries/}").query("{uri_contains:data.json}").apply(new ZebedeeTimeSeriesStripper(db));

        // Republish all the data
        scripts.publishAllData();

    }

    private static void migrateTimeSerieses(FlatsyDatabase dbFrom, FlatsyDatabase dbTo) {

    }
    private static void migrateCSDBDatasets(FlatsyDatabase dbFrom, FlatsyDatabase dbTo) {

    }
}
