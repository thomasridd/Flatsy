package com.github.thomasridd.flatsy.utils;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.operations.operators.ReplaceIn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by thomasridd on 26/08/15.
 */
public class FlatsyMoverTool {
    List<FromTo> fromToList;

    public FlatsyMoverTool(Map<String, String> fromTo) {
        this.fromToList = new ArrayList<>();
        for (String key : fromTo.keySet()) {
            this.fromToList.add(new FromTo(key, fromTo.get(key)));
        }
    }

    public FlatsyMoverTool(Path path) throws IOException {
        this(path, false);
    }

    public FlatsyMoverTool(Path path, boolean hasHeader) throws IOException {
        this.fromToList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            if (hasHeader) {
                String header = br.readLine();
            }

            for (String line; (line = br.readLine()) != null; ) {
                // process the line.
                String[] split = line.split("\t");
                if (split.length > 1) {
                    this.fromToList.add(new FromTo(split[0], split[1]));
                }
            }
        }
    }

    void moveFiles(FlatsyDatabase db) {
        Collections.sort(fromToList);

        for (FromTo fromTo : fromToList) {
            // make the move
            db.move(db.get(fromTo.fromUri), fromTo.toUri);

            // update file contents
            db.root().cursor().query("{uri_contains:.json}").apply(new ReplaceIn(fromTo.fromUri, fromTo.toUri));
        }
    }

    public void move(FlatsyDatabase db) {
        moveFiles(db);
    }

    class FromTo implements Comparable {
        public String fromUri;
        public String toUri;

        public FromTo(String fromUri, String toUri) {
            this.fromUri = fromUri;
            this.toUri = toUri;
        }

        @Override
        public int compareTo(Object o) {
            FromTo fromTo = (FromTo) o;
            int selfSegments = fromUri.split("/").length;
            int otherSegments = fromTo.fromUri.split("/").length;


            if (selfSegments == otherSegments) {
                return fromUri.compareTo(fromTo.fromUri);
            } else if (otherSegments > selfSegments) {
                return 1;
            } else {
                return -1;
            }
        }

    }


}
