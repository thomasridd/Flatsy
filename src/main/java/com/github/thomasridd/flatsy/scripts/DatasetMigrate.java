package com.github.thomasridd.flatsy.scripts;

import au.com.bytecode.opencsv.CSVReader;
import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.operations.operators.FlatsyOperator;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class DatasetMigrate {



    public static void findAllDatasets() {
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master"));

        db.root().query("{uri_contains:datasets}").query("{uri_contains:data.json}").apply("{json_paths_to_console:[\"$.description.title\"]}");
    }

    public static void findDatasetDownloads() {
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master"));

        final List<String> paths = new ArrayList<>();
        paths.add("$.description.title"); paths.add("$.type");

        final List<String> subpaths = new ArrayList<>();
        subpaths.add("title"); subpaths.add("file");

        db.root().query("{uri_contains:datasets}").query("{uri_contains:data.json}").query("{json_equals:$.type=dataset}").apply(new FlatsyOperator() {
            @Override
            public void apply(FlatsyObject object) {

                try {
                    // Read all the data
                    String objectString = object.uri + "\t";
                    DocumentContext context = JsonPath.parse(object.retrieveStream());

                    // Extract
                    for (String path: paths) {
                        try {
                            String value = context.read(path);
                            value = value.replace("\n", " ").replace("\r", " ");
                            objectString += value + "\t";
                        } catch (com.jayway.jsonpath.PathNotFoundException e) {
                            objectString += "\t";
                        }
                    }


                    JSONArray objects = context.read("$.downloads");
                    for (Object arrayObject: objects) {
                        String fullString = objectString;

                        HashMap<String, String> hashMap = (LinkedHashMap) arrayObject;
                        for (String path: subpaths) {
                            if (hashMap.keySet().contains(path)) {
                                String value = hashMap.get(path).toString().replace("\n", " ").replace("\r", " ");
                                fullString += value + "\t";
                            } else {
                                fullString += "\t";
                            }
                        }

                        System.out.println(fullString);
                    }


                } catch (IOException e) {
                    System.out.println("Failed to print for uri: " + object.uri);
                }

            }
        });
    }

    public static List<DataFile> dataFilesFromCSV(String csvFilename) throws IOException {

        List<DataFile> dataFiles = new ArrayList<>();

        try(CSVReader csvReader = new CSVReader(new FileReader(csvFilename))) {
            csvReader.readNext();

            String[] row = null;
            while ((row = csvReader.readNext()) != null) {
                DataFile dataFile = new DataFile();
                dataFile.pageUri = row[1];
                dataFile.pageTitle = row[3];
                dataFile.downloadTitle = row[4];
                dataFile.subpageTitle = row[5];
                dataFile.downloadFileName = row[6];
                dataFiles.add(dataFile);
            }
        }

        return dataFiles;
    }


    public static void main(String[] args) throws IOException {
        String csvFilename = "/Users/thomasridd/Documents/onswebsite/dataset_migration.csv";
        Path root = Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master");


        List<DataFile> dataFiles = DatasetMigrate.dataFilesFromCSV(csvFilename);

        int i = 0;
        for (DataFile dataFile: dataFiles) {
            dataFile.subfolderCopy(root);

            if (++i >= 3) { return;}
        }
    }
}
