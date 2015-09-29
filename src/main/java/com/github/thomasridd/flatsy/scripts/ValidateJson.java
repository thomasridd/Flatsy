package com.github.thomasridd.flatsy.scripts;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.operations.operators.JSONValidate;

import java.nio.file.Paths;

/**
 * Created by thomasridd on 17/09/15.
 */
public class ValidateJson {
    public static void main(String[] args) {
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/"));
        db.root().query("{uri_ends:json}").query("{is_file}").apply(new JSONValidate());
    }
}
