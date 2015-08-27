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
        Path tempDir = Files.createTempDirectory("flatsytest");
        return copyFlatFiles(tempDir);
    }
    public static Path copyFlatFiles(Path toPath) throws IOException {
        URL flatfiletest = Builder.class.getResource("/flatsytest");
        FileUtils.copyDirectory(Paths.get(flatfiletest.getPath()).toFile(), toPath.toFile());
        return toPath;
    }
    public static Path refreshFlatFiles(Path atPath) throws IOException {
        FileUtils.deleteDirectory(atPath.toFile());
        return copyFlatFiles(atPath);
    }

    public static Path cursorTestDatabase() throws IOException {
        Path tempDir = Files.createTempDirectory("flatsytest");

        FlatsyDatabase db = new FlatsyFlatFileDatabase(tempDir);
        db.create(new FlatsyObject("alpha/one.json", db), "Alpha1");
        db.create(new FlatsyObject("alpha/two.json", db), "Alpha2");
        db.create(new FlatsyObject("beta/three.json", db), "Beta1");
        db.create(new FlatsyObject("beta/four.json", db), "Beta2");

        return tempDir;
    }

    public static Path emptyTestDatabase() throws IOException {
        Path tempDir = Files.createTempDirectory("flatsyempty");

        FlatsyDatabase db = new FlatsyFlatFileDatabase(tempDir);

        return tempDir;
    }
}
