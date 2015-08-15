package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.update.FlatsyUpdate;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

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
    public String retrieve(FlatsyObject object) {
        return null;
    }

    @Override
    public String retrieve(FlatsyObject object, OutputStream stream) {
        return null;
    }

    @Override
    public <T> Object retrieveAs(String uri, Class<T> tClass) {
        return null;
    }

    @Override
    public void update(String uri, FlatsyUpdate update) {

    }

    @Override
    public void delete(String uri) {

    }

    @Override
    public List<FlatsyObject> subObjects(FlatsyObject object) {
        return null;
    }
}
