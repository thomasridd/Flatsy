package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.query.FlatsyCursor;
import com.github.thomasridd.flatsy.query.matchers.MatcherCommandLineParser;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by thomasridd on 26/10/15.
 */
public class ScriptsTest {
    Path root = null;

    @Before
    public void setUp() throws Exception {
        // For all tests we copy the flatFileTest example dataset
        root = Builder.copyDatasetFiles();
    }

    @After
    public void tearDown() throws Exception {
        // Garbage collection (try to avoid choking the file system)
        FileUtils.deleteDirectory(root.toFile());
    }

    @Test
    public void commandLineForDatasets_givenFolderWithDatasets_returnsResults() throws IOException {
        // Given
        // a database
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        // When
        // we get our script of datasetFiles
        List<FlatsyObject> flatsyObjects = Scripts.datasetFiles(root.toString());

        // Then
        // we should get a list of files
        assertNotEquals(0, flatsyObjects.size());

    }

    @Test
    public void copyDatasetToSubfolders_givenFolderWithDatasets_movesFiles() throws IOException, URISyntaxException {
        // Given
        // a database
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        // When
        // we get our script of datasetFiles
        List<FlatsyObject> flatsyObjects = Scripts.datasetFiles(root.toString());
        for (FlatsyObject obj: flatsyObjects) {
            Scripts.copyDatasetToSubfolders(obj);

        }

        System.out.println(root.toString());
        // Then
        // we should get a list of files
        assertNotEquals(0, flatsyObjects.size());

    }

    @Test
    public void copyTimeseresDatasetToSubfolders_givenFolderWithDatasets_movesFiles() throws IOException, URISyntaxException {
        // Given
        // a database
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        // When
        // we get our script of datasetFiles
        List<FlatsyObject> flatsyObjects = Scripts.timeseriesDatasets(root.toString());
        for (FlatsyObject obj: flatsyObjects) {
            Scripts.copyTimeseriesDatasetToSubfolders(obj);
        }

        System.out.println(root.toString());
        // Then
        // we should get a list of files
        assertNotEquals(0, flatsyObjects.size());

    }
}