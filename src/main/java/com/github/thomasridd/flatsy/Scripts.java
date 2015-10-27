package com.github.thomasridd.flatsy;

import com.github.onsdigital.zebedee.content.page.statistics.dataset.Dataset;
import com.github.onsdigital.zebedee.content.page.statistics.dataset.DatasetLandingPage;
import com.github.onsdigital.zebedee.content.page.statistics.dataset.DownloadSection;
import com.github.onsdigital.zebedee.content.partial.Link;
import com.github.onsdigital.zebedee.content.util.ContentUtil;
import com.github.thomasridd.flatsy.operations.operators.CopyTo;
import com.github.thomasridd.flatsy.operations.operators.Delete;
import com.github.thomasridd.flatsy.operations.operators.Replace;
import com.github.thomasridd.flatsy.util.FlatsyUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thomasridd on 23/10/15.
 */
public class Scripts {

    public static List<FlatsyObject> datasetFiles(String root) {
        FlatsyCommandLine cli = new FlatsyCommandLine();
        cli.runCommand("from " + root);
        cli.runCommand("filter uri_contains /datasets/");
        cli.runCommand("filter uri_ends /data.json");
        cli.runCommand("filter jsonpath $.type equals dataset");
        List<FlatsyObject> all = cli.cursor().getAll();

        List<FlatsyObject> doUpdate = new ArrayList<>();
        for (FlatsyObject obj: all) {
            if (obj.parent().parent().uri.endsWith("/datasets")) {
                doUpdate.add(obj);
            }
        }

        return doUpdate;
    }

    public static boolean copyDatasetToSubfolders(FlatsyObject datasetObj) throws IOException, URISyntaxException {
        Map<String, String> oldUriNewUri = new HashMap<>();

        try (InputStream stream = datasetObj.retrieveStream()) {

            Delete delete = new Delete(datasetObj.db);


            Dataset dataset = ContentUtil.deserialise(stream, Dataset.class);
            for (DownloadSection download: dataset.getDownloads()) {

                FlatsyObject file = datasetObj.db.get(download.getFile());
                String folder = cleanString(download.getTitle());

                String newUri = FlatsyUtil.stringExpression("~.parent + /" + folder + "/ + ~.file", file);
                oldUriNewUri.put(file.uri, newUri);

                CopyTo copyTo = new CopyTo(datasetObj.db, "~.parent + /" + folder + "/ + ~.file");
                copyTo.apply(datasetObj);
                copyTo.apply(file);

                delete.apply(file);
            }

            // Update uri's in files
            List<FlatsyObject> subfiles = subDatasets(datasetObj);
            for (FlatsyObject subfile: subfiles) {
                Replace replace = new Replace(datasetObj.parent().uri, datasetObj.parent().uri + "/" + subfile.parent().uri);
                replace.apply(subfile);

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
                if (oldUri.startsWith("/")) oldUri = oldUri.substring(1);
                datasets.add(new Link(new URI(oldUriNewUri.get(oldUri))));
            }
//            for (FlatsyObject object: datasetObj.parent().children()) {
//                if (object.getType() == FlatsyObjectType.Folder) {
//                    datasets.add( new Link(new URI(object.uri)));
//                }
//            }

            landingPage.setDownloads(null);
            landingPage.setDatasets(datasets);
        }
        Delete delete = new Delete(datasetObj.db);
        delete.apply(datasetObj);
        datasetObj.create(ContentUtil.serialise(landingPage));

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
                if (root.get(section.getFile()).getType() != FlatsyObjectType.Null) {
                    newDownloads.add(section);
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

    public static void main(String[] args) {

    }
}
