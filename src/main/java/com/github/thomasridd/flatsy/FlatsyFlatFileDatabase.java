package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.operations.operators.FlatsyOperator;
import com.github.thomasridd.flatsy.operations.operators.UriToMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Tom.Ridd on 15/08/15.
 */
public class FlatsyFlatFileDatabase implements FlatsyDatabase {
    Path root;

    public FlatsyFlatFileDatabase(Path root) {
        this.root = root;
    }


    @Override
    public FlatsyObject root() {
        return get("");
    }
    @Override
    public FlatsyObject get(String uri) {
        return new FlatsyObject(uri, this);
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
    public void update(FlatsyObject object, FlatsyOperator update) {
        object.apply(update);
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
    public List<FlatsyObject> children(FlatsyObject object) {
        List<FlatsyObject> objects = new ArrayList<>();
        Path path = toPath(object.uri);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path p : directoryStream) {
                objects.add(new FlatsyObject(this.root.relativize(p).toString(), this));
            }
        } catch (IOException ex) {}
        return objects;
    }

    /**
     * Update an object uri and all child objects
     *
     * @param object
     * @param newUri
     */
    @Override
    public void move(FlatsyObject object, String newUri) {
        // File system kicks in for a straight path move
        Path oldPath = root.resolve(object.uri);
        Path newPath = root.resolve(newUri);
        try {
            if (object.getType() == FlatsyObjectType.Folder) {

                FileUtils.moveDirectory(oldPath.toFile(), newPath.toFile());

            } else if (object.getType() == FlatsyObjectType.JSONFile || object.getType() == FlatsyObjectType.OtherFile) {

                FileUtils.moveFile(oldPath.toFile(), newPath.toFile());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * Get the mapping that would move an object and all subfiles to a new uri
     *
     * @param object an object that is going to be moved
     * @param newUri the uri it will be moved to
     * @return A map of form moveMap.get(oldUri) = newUri
     */
    Map<String, String> moveMap(FlatsyObject object, String newUri) {

        ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

        object.apply(new UriToMap(map, newUri));

        for (String key : map.keySet()) {
            String newFilename = moveFilename(new FlatsyObject(key, object.db), object.uri, newUri);
            map.put(key, newFilename);
        }

        return map;
    }

    /**
     * Get the filename that objectToMove will need when moved as part of the
     * operation oldUri -> newUri
     * <p/>
     * oldUri must be a parent path of the object
     *
     * @param objectToMove a subobject to be moved
     * @param oldUri       the 'from' uri
     * @param newUri       the 'to' uri
     * @return
     */
    String moveFilename(FlatsyObject objectToMove, String oldUri, String newUri) {
        if (!objectToMove.uri.startsWith(oldUri)) {
            return objectToMove.uri;
        }
        return newUri + objectToMove.uri.substring(oldUri.length());
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
