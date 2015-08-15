package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.update.FlatsyUpdate;
import sun.misc.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

    }

    @Override
    public void create(FlatsyObject object, InputStream content) {

    }

    @Override
    public String retrieve(FlatsyObject object)  {
        Path path = this.root.resolve(object.uri);

        String content = null;
        try {
            content = new Scanner(path.toFile()).useDelimiter("//Z").next();
        } catch (FileNotFoundException e) {
            return null;
        }
        return content;
    }

    @Override
    public String retrieve(FlatsyObject object, OutputStream stream) {
        return null;
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

    }

    @Override
    public List<FlatsyObject> subObjects(FlatsyObject object) {
        return null;
    }
}
