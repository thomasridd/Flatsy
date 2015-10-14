package com.github.thomasridd.flatsy.scripts;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.operations.operators.UriToOutput;
import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.query.matchers.JSONPathOneOf;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasridd on 20/08/15.
 */
public class FileSearch {
    public static void main(String[] args) {
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master"));

        FlatsyCursor cur = db.root().query("block:{uri_contains:timeseries}");

        List<String> options = new ArrayList<>();
        options.add("reference_tables");

        cur.query(new JSONPathOneOf("$.type", options)).apply(new UriToOutput(System.out));
    }
}
