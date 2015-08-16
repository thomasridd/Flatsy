package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.update.FlatsyUpdate;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyFlatFileDatabase implements FlatsyDatabase {
    Path root;

    public FlatsyFlatFileDatabase(Path root) {
        this.root = root;
    }


    @Override
    public FlatsyObject rootObject() {
        return new FlatsyObject("", this);
    }

    @Override
    public void create(FlatsyObject object, String content) {
        Path path = toPath(object.uri);

        try {
            path.toFile().getParentFile().mkdirs();
            Files.write(path, content.getBytes());
        } catch (IOException e) {
            System.out.println("Error in create " + object.uri);
        }
    }

    @Override
    public void create(FlatsyObject object, InputStream content) throws IOException {
        Path path = toPath(object.uri);

        path.toFile().getParentFile().mkdirs();
        try(OutputStream outputStream = Files.newOutputStream(path)) {
            IOUtils.copy(content, outputStream);
        }
    }

    @Override
    public String retrieve(FlatsyObject object)  {
        Path path = toPath(object.uri);

        String content = null;
        try {
            content = new Scanner(path.toFile()).useDelimiter("//Z").next();
        } catch (FileNotFoundException e) {
            return null;
        }
        return content;
    }

    @Override
    public InputStream retrieveStream(FlatsyObject object) throws IOException {
        Path path = toPath(object.uri);
        return Files.newInputStream(path);
    }

    @Override
    public <T> Object retrieveAs(FlatsyObject object, Class<T> tClass) {
        return null;
    }

    @Override
    public void update(FlatsyObject object, FlatsyUpdate update) {

    }

    @Override
    public void delete(FlatsyObject object) {
        Path path = toPath(object.uri);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.out.println("error in delete: " + object.uri);
        }
    }

    @Override
    public List<FlatsyObject> subObjects(FlatsyObject object) {
        List<FlatsyObject> objects = new ArrayList<>();
        Path path = toPath(object.uri);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path p : directoryStream) {
                objects.add(new FlatsyObject(this.root.relativize(p).toString(), this));
            }
        } catch (IOException ex) {}
        return objects;
    }

    @Override
    public FlatsyObjectType type(String uri) {
        Path path = this.root.resolve(uri);

        if (!Files.exists(path)) {
            return FlatsyObjectType.Null;
        } else if(Files.isDirectory(path)) {
            return FlatsyObjectType.Folder;
        } else if(uri.endsWith(".json")) {
            return FlatsyObjectType.JSONFile;
        } else {
            return FlatsyObjectType.OtherFile;
        }
    }

    private Path toPath(String uri) {
        if (uri.startsWith("/")) {
            return root.resolve(uri.substring(1));
        } else {
            return root.resolve(uri);
        }
    }
}
