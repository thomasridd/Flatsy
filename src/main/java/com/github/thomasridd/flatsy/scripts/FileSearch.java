package com.github.thomasridd.flatsy.scripts;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.operations.operators.UriToOutput;
import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.query.matchers.JSONPathReturnsOne;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasridd on 20/08/15.
 */
public class FileSearch {
    public static void main(String[] args) {
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master"));

        FlatsyCursor cur = db.root().query("block:{uri_contains:timeseries}").query("block:{uri_contains:datasets}");

        List<String> options = new ArrayList<>();
        options.add("bulletins");

        cur.query(new JSONPathReturnsOne("$.relatedArticles")).apply(new UriToOutput(System.out));
    }
}
