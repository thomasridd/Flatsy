package com.github.thomasridd.flatsy.utils;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;
import com.github.thomasridd.flatsy.operations.operators.ReplaceIn;
import com.github.thomasridd.flatsy.operations.operators.UriToMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by thomasridd on 26/08/15.
 *
 * The mover tool is very crude
 *
 * All it does is move the files then replace all references to files on the file system
 *
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

    public static void printProblemsWithCollections() throws IOException {
        Path root = Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/collections");
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        Path updateFile = Paths.get("/Users/thomasridd/Documents/onswebsite/uri_updates.txt");
        FlatsyMoverTool mover = new FlatsyMoverTool(updateFile, true);


        for (FlatsyObject collection : db.root().children()) {
            System.out.println("Checking collection " + collection.uri);
            for (FlatsyObject content : collection.children()) {
                Path subPath = root.resolve(content.uri);
                FlatsyDatabase subDb = new FlatsyFlatFileDatabase(subPath);

                mover.movesAreValid(subDb);

                Map<FromTo, List<String>> fromToListMap = mover.contentUpdateList(subDb);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Path root = Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/launchpad");
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        Path updateFile = Paths.get("/Users/thomasridd/Documents/onswebsite/uri_updates.txt");
        FlatsyMoverTool mover = new FlatsyMoverTool(updateFile, true);


        if (mover.movesAreValid(db)) {
            mover.move(db);
        }
    }

    public void move(FlatsyDatabase db) {
        move(db, true);
    }

    public void move(FlatsyDatabase db, boolean updateContents) {
        Collections.sort(fromToList);

        for (FromTo fromTo : fromToList) {
            System.out.println("moving " + fromTo.fromUri + " to " + fromTo.toUri);
            // make the move
            db.move(db.get(fromTo.fromUri), fromTo.toUri);

            // update file contents
            if (updateContents) {
                db.root().cursor().query("{uri_contains:.json}").apply(new ReplaceIn(fromTo.fromUri, fromTo.toUri));
            }
        }
    }

    /**
     * get a map of where any updates would be made in file content
     *
     * @param db - a database
     * @return Map (The FromTo Operation) -> (List of URIs which will be replaced)
     */
    public Map<FromTo, List<String>> contentUpdateList(FlatsyDatabase db) {
        Map<FromTo, List<String>> results = new HashMap<>();

        long start = System.currentTimeMillis();

        for (FromTo fromTo : fromToList) {
            // get a map of the uris
            ConcurrentMap<String, String> urisWithReplaceMents = new ConcurrentHashMap<>();
            db.root().cursor().query("{uri_contains:.json}").query("{content_contains:" + fromTo.fromUri + "}").apply(new UriToMap(urisWithReplaceMents, fromTo.fromUri));

            for (String key : urisWithReplaceMents.keySet()) {
                System.out.println("Replacement:\t" + fromTo.fromUri + "\t in \t" + key);
            }

            // add them to the list
            List<String> uris = new ArrayList<>();
            for (String uri : urisWithReplaceMents.keySet()) {
                uris.add(uri);
            }

            // assign
            results.put(fromTo, uris);
        }
        return results;
    }

    /**
     * check the from uris exist
     *
     * @param db
     * @return
     */
    public boolean movesAreValid(FlatsyDatabase db) {
        boolean valid = true;

        for (FromTo fromTo : fromToList) {
            if (db.get(fromTo.fromUri).getType() == FlatsyObjectType.Null) {
                valid = false;
            } else {
                System.out.println(fromTo.fromUri + " exists in a collection and cannot be moved");
            }

        }
        return valid;
    }

    class FromTo implements Comparable {
        public String fromUri;
        public String toUri;

        public FromTo(String fromUri, String toUri) {
            this.fromUri = fromUri;
            this.toUri = toUri;

            if (this.fromUri.startsWith("/")) {
                this.fromUri = this.fromUri.substring(1);
            }
            if (this.toUri.startsWith("/")) {
                this.toUri = this.toUri.substring(1);
            }
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