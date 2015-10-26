package com.github.thomasridd.flatsy;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;

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
    public void __() {
        // Given
        // 

        // When
        // 

        // Then
        // 

    }
}