package com.github.thomasridd.flatsy;

import com.github.onsdigital.zebedee.content.page.statistics.dataset.Dataset;
import com.github.onsdigital.zebedee.content.page.statistics.dataset.DatasetLandingPage;
import com.github.onsdigital.zebedee.content.page.statistics.dataset.DownloadSection;
import com.github.onsdigital.zebedee.content.page.statistics.dataset.TimeSeriesDataset;
import com.github.onsdigital.zebedee.content.partial.Link;
import com.github.onsdigital.zebedee.content.util.ContentUtil;
import com.github.thomasridd.flatsy.operations.operators.*;
import com.github.thomasridd.flatsy.util.FlatsyUtil;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.DELETE;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thomasridd on 23/10/15.
 */
public class Scripts {

    /**
     * get a list of datasets that need processing
     *
     * @param root
     * @return
     */
    public static List<FlatsyObject> datasetFiles(String root) {
        // Basic search
        FlatsyCommandLine cli = new FlatsyCommandLine();
        cli.runCommand("from " + root);
        cli.runCommand("filter uri_contains /datasets/");
        cli.runCommand("filter uri_ends /data.json");
        cli.runCommand("filter jsonpath $.type equals dataset");
        List<FlatsyObject> all = cli.cursor().getAll();

        // Filter out datasets that have already been processed
        List<FlatsyObject> doUpdate = new ArrayList<>();
        for (FlatsyObject obj: all) {
            if (obj.parent().parent().uri.endsWith("/datasets")) {
                doUpdate.add(obj);
            }
        }

        return doUpdate;
    }

    /**
     * get a list of timeseries datasets that need processing
     *
     * @param root
     * @return
     */
    public static List<FlatsyObject> timeseriesDatasets(String root) {
        FlatsyCommandLine cli = new FlatsyCommandLine();
        cli.runCommand("from " + root);
        cli.runCommand("filter uri_contains /datasets/");
        cli.runCommand("filter uri_ends /data.json");
        cli.runCommand("filter jsonpath $.type equals timeseries_dataset");
        List<FlatsyObject> all = cli.cursor().getAll();

        List<FlatsyObject> doUpdate = new ArrayList<>();
        for (FlatsyObject obj: all) {
            if (obj.parent().parent().uri.endsWith("/datasets")) {
                doUpdate.add(obj);
            }
        }

        return doUpdate;
    }

    /**
     *
     * @param datasetObj
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static boolean copyDatasetToSubfolders(FlatsyObject datasetObj) throws IOException, URISyntaxException {

        Map<String, String> oldUriNewJsonUri = new HashMap<>();
        Map<String, String> oldUriNewFolder = new HashMap<>();

        Map<String, String> newUriDownloadTitle = new HashMap<>();
        Map<String, String> newJsonUriFilename = new HashMap<>();

        Map<String, String> newJsonUriOldFileUri = new HashMap<>();
        Map<String, String> newJsonUriNewFileUri = new HashMap<>();

        Map<String, String> newJsonUriNewUri = new HashMap<>();
        Map<String, String> newJsonUriOldUri = new HashMap<>();

        try (InputStream stream = datasetObj.retrieveStream()) {

            Delete delete = new Delete(datasetObj.db);


            Dataset dataset = ContentUtil.deserialise(stream, Dataset.class);
            for (DownloadSection download: dataset.getDownloads()) {
                System.out.println("      shifting: " + download.getFile() );
                // Find the files we are going to be shifting
                if (download.getFile() != null) {
                    FlatsyObject file = datasetObj.db.get(download.getFile());
                    String folder = cleanString(download.getTitle());
                    if (folder.equalsIgnoreCase("latest") || folder.toLowerCase().equalsIgnoreCase("latest") || folder.equalsIgnoreCase("data")) {
                        folder = "current";
                    }

                    // Get a plan
                    String newJsonUri = FlatsyUtil.stringExpression("~.parent + /" + folder + "/data.json", file);
                    String newFileUri = FlatsyUtil.stringExpression("~.parent + /" + folder + "/ + ~.file", file);
                    String newUri = FlatsyUtil.stringExpression("~.parent + /" + folder, file);
                    String oldUri = FlatsyUtil.stringExpression("~.parent", file);

                    oldUriNewJsonUri.put(file.uri, newJsonUri);
                    oldUriNewFolder.put(file.uri, FlatsyUtil.stringExpression("~.parent + /" + folder, file));
                    newJsonUriOldFileUri.put(newJsonUri, file.uri);
                    newJsonUriFilename.put(newJsonUri, FlatsyUtil.stringExpression("~.file", file));
                    newJsonUriNewFileUri.put(newJsonUri, newFileUri);

                    newJsonUriNewUri.put(newJsonUri, newUri);
                    newJsonUriOldUri.put(newJsonUri, oldUri);

                    newUriDownloadTitle.put(newJsonUri, download.getTitle());

                    // Do the copying
                    CopyTo copyTo = new CopyTo(datasetObj.db, "~.parent + /" + folder + "/ + ~.file");
                    copyTo.apply(datasetObj);
                    copyTo.apply(file);

                    delete.apply(file);
                }
            }

            // Edit dataset files
            List<FlatsyObject> subfiles = subDatasets(datasetObj);
            for (FlatsyObject subfile: subfiles) {
                String subfileFullUri = datasetObj.parent().uri + "/" + subfile.uri;

                Replace replace = new Replace(newJsonUriOldUri.get(subfileFullUri),
                        newJsonUriNewUri.get(subfileFullUri));
                replace.apply(subfile);

                String newTitle = newUriDownloadTitle.get(datasetObj.parent().uri + "/" + subfile.uri);
                if(newTitle.trim().toLowerCase().equalsIgnoreCase("latest") || newTitle.trim().equalsIgnoreCase("data")) newTitle = "Current";
                JSONPathPut addEdition = new JSONPathPut("$.description", "edition", newTitle);
                addEdition.apply(subfile);

                // Strip out all downloads except their own file
                reformatSubdatasetDownloads(subfile, datasetObj.db);
            }
        }

        DatasetLandingPage landingPage;
        try (InputStream stream = datasetObj.retrieveStream()) {

            landingPage = ContentUtil.deserialise(stream, DatasetLandingPage.class);
            List<Link> datasets = new ArrayList<>();

            for (DownloadSection download: landingPage.getDownloads()) {
                String oldUri = download.getFile();
                if (oldUri != null) {
                    if (oldUri.startsWith("/")) oldUri = oldUri.substring(1);

                    datasets.add(new Link(new URI("/" + oldUriNewFolder.get(oldUri))));
                }
            }

            landingPage.setDownloads(null);
            landingPage.setDatasets(datasets);
        }
        Delete delete = new Delete(datasetObj.db);
        delete.apply(datasetObj);
        datasetObj.create(ContentUtil.serialise(landingPage));

        // Update the path (can't be done in
        JSONPathPut jsonPathPut = new JSONPathPut("$", "type", "dataset_landing_page");
        jsonPathPut.apply(datasetObj);

        return  false;
    }

    /**
     *
     * @param jsonObj
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static boolean copyTimeseriesDatasetToSubfolders(FlatsyObject jsonObj) throws IOException, URISyntaxException {

        // Read in the data as a landing page
        DatasetLandingPage landingPage = null;
        try (InputStream stream = jsonObj.retrieveStream()) {
            landingPage = ContentUtil.deserialise(stream, DatasetLandingPage.class);
        }

        // Read in the data as a
        TimeSeriesDataset dataset = null;
        try (InputStream stream = jsonObj.retrieveStream()) {
            dataset = ContentUtil.deserialise(stream, TimeSeriesDataset.class);
        }

        // Bomb out if null
        if (landingPage == null || dataset == null) {
            System.out.println("Problems deserialising timeseries dataset. Bombing out");
            return false;
        }

        // Prepare a list of files that will need dropping
        FlatsyObject folderObj = jsonObj.parent();
        List<FlatsyObject> moveObjs = folderObj.children();

        // Do the moving
        CopyTo copyTo = new CopyTo(folderObj.db, "~.parent + /current/ + ~.file");
        List<Replace> replaceList = new ArrayList<>();
        Delete delete = new Delete(folderObj.db);
        for (FlatsyObject moveObj: moveObjs) {
            copyTo.apply(moveObj);
            delete.apply(moveObj);

            if(moveObj.uri != jsonObj.uri) {
                replaceList.add(new Replace(moveObj.uri,
                        FlatsyUtil.stringExpression("~.parent + /current/ + ~.file", moveObj)));
            }
        }

        FlatsyObject movedObj = new FlatsyObject(FlatsyUtil.stringExpression("/ + ~.parent + /current/ + ~.file", jsonObj), jsonObj.db);
        for (Replace replace: replaceList) {
            replace.apply(movedObj);
        }

        JSONPathRemove jsonPathRemove = new JSONPathRemove("$", "section"); jsonPathRemove.apply(movedObj);
        jsonPathRemove = new JSONPathRemove("$", "relatedDatasets"); jsonPathRemove.apply(movedObj);
        jsonPathRemove = new JSONPathRemove("$", "relatedDocuments"); jsonPathRemove.apply(movedObj);


        List<Link> datasets =  new ArrayList<>();
        datasets.add(new Link(new URI(FlatsyUtil.stringExpression("/ + ~.parent + /current", jsonObj))));
        landingPage.setDatasets(datasets);
        landingPage.setDownloads(null);
        landingPage.setTimeseries(true);

        // Save the landing page
        String landingPageJson = ContentUtil.serialise(landingPage);
        jsonObj.create(landingPageJson);

        // Update the type (not possible in serialiser)
        JSONPathPut jsonPathPut = new JSONPathPut("$", "type", "dataset_landing_page");
        jsonPathPut.apply(jsonObj);


        return  false;
    }

    public static List<FlatsyObject> subDatasets(FlatsyObject parent) {

        FlatsyCommandLine cli = new FlatsyCommandLine();
        cli.runCommand("from " + parent.db.toString() + "/" + parent.parent().uri);
        cli.runCommand("filter uri_ends /data.json");
        cli.runCommand("filter jsonpath $.type equals dataset");
        List<FlatsyObject> all = cli.cursor().getAll();

        List<FlatsyObject> doUpdate = new ArrayList<>();
        for (FlatsyObject obj: all) {
            if (!obj.parent().parent().uri.endsWith("/datasets")) {
                doUpdate.add(obj);
            }
        }

        return doUpdate;
    }

    public static void reformatSubdatasetDownloads(FlatsyObject subdataset, FlatsyDatabase root) {
        try(InputStream stream = subdataset.retrieveStream()) {
            Dataset dataset = ContentUtil.deserialise(stream, Dataset.class);
            List<DownloadSection> downloads = dataset.getDownloads();
            List<DownloadSection> newDownloads = new ArrayList<>();

            // Update the downloads section for all files that exist
            for (DownloadSection section: downloads) {
                FlatsyObject obj = root.get(section.getFile());
                if (obj.getType() != FlatsyObjectType.Null) {
                    DownloadSection newSection = new DownloadSection();

                    String title = section.getTitle().trim().toLowerCase();

                    if (title.trim().toLowerCase().equalsIgnoreCase("latest") || title.equalsIgnoreCase("data")){
                        newSection.setTitle("Current");
                    } else {
                        newSection.setTitle(section.getTitle());
                    }

                    newSection.setFileDescription(section.getFileDescription());
                    newSection.setFile(FlatsyUtil.stringExpression("~.file", obj));

                    newDownloads.add(newSection);
                }
            }
            dataset.setDownloads(newDownloads);

            subdataset.create(ContentUtil.serialise(dataset));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String cleanString(String string) {
        final int MAX_LENGTH = 30;

        StringBuilder filename = new StringBuilder();

        // Strip dodgy characters:
        for (char c : string.toCharArray()) {
            if (c == '.' || Character.isJavaIdentifierPart(c)) {
                filename.append(c);
            }
        }

        // Ensure the String is a sensible length:
        return StringUtils.lowerCase(StringUtils.abbreviateMiddle(filename.toString(), "_",
                MAX_LENGTH));
    }

    public static void quickReplaceInJson(Path root, String oldValue, String newValue) {
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        Replace replace = new Replace(oldValue, newValue);
        FlatsyCommandLine cli = new FlatsyCommandLine();
        cli.runCommand("from " + root.toString());
        cli.runCommand("filter files");
        cli.runCommand("filter uri_ends data.json");
        cli.runCommand("replace " + oldValue + " " + newValue);
    }
    public static void quickJsonPut(Path root, String jsonRoot, String field, String newValue) {
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        JSONPathPut jsonPathPut = new JSONPathPut(jsonRoot, field, newValue);

        FlatsyCommandLine cli = new FlatsyCommandLine();
        cli.runCommand("from " + root.toString());
        cli.runCommand("filter files");
        cli.runCommand("filter uri_ends data.json");
        cli.cursor().apply(jsonPathPut);
    }

    public static void main(String[] args) throws IOException, URISyntaxException {

        // Fix up some things
        quickReplaceInJson(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master/peoplepopulationandcommunity/wellbeing/datasets"),
                "/archive/peoplepopulationandcommunity/wellbeing/", "/peoplepopulationandcommunity/wellbeing/");
        quickJsonPut(Paths.get(" /Users/thomasridd/Documents/onswebsite/zebedee/master/economy/nationalaccounts/uksectoraccounts/datasets/profitabilityofukcompanies"),
                "$", "type", "timeseries_dataset");

        // Set up the database
        Path root = Paths.get("/Users/thomasridd/git/zebedee/zebedee-cms/src/test/resources/bootstraps");
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        // Part one - fix up the regular datasets
        List<FlatsyObject> flatsyObjects = Scripts.datasetFiles(root.toString());
        for (FlatsyObject obj: flatsyObjects) {
            System.out.println("Moving to subfolders: " + obj.uri);
            Scripts.copyDatasetToSubfolders(obj);
        }

        // Part two - fix up the timeseries datasets
        flatsyObjects = Scripts.timeseriesDatasets(root.toString());
        for (FlatsyObject obj: flatsyObjects) {
            System.out.println("Moving to subfolders: " + obj.uri);
            Scripts.copyTimeseriesDatasetToSubfolders(obj);
        }

        System.out.println(root.toString());
    }
}
