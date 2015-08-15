package com.github.thomasridd.flatsy;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class Builder {
    public static Path copyFlatFiles() throws IOException {
        Path tempDir = Files.createTempDirectory("flatfiletest");
        return copyFlatFiles(tempDir);
    }
    public static Path copyFlatFiles(Path toPath) throws IOException {
        URL flatfiletest = Builder.class.getResource("/flatfiletest");
        FileUtils.copyDirectory(Paths.get(flatfiletest.getPath()).toFile(), toPath.toFile());
        return toPath;
    }
    public static Path refreshFlatFiles(Path atPath) throws IOException {
        FileUtils.deleteDirectory(atPath.toFile());
        return copyFlatFiles(atPath);
    }
}
