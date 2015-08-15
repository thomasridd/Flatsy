package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.update.FlatsyUpdate;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by Tom.Ridd on 15/08/15.
 *
 * FlatsyDatabase is document store that works, initially, from file system
 * When your project expands Flatsy is sufficiently extensible
 *
 * FlatsyFlatFileDatabase:
 * Flatsy
 */
public interface FlatsyDatabase {
    public FlatsyObject rootObject();

    public void create(FlatsyObject object, String content);
    public void create(FlatsyObject object, InputStream content);

    /**
     * Retrieves the string contents for the FlatsyObject database object
     * Suitable for deserialising objects from JSON
     *
     * @param object a FlatsyObject with valid URI
     * @return
     */
    public String retrieve(FlatsyObject object);

    /**
     * Writes the content of the object to an OutputStream.
     * This is particularly applicable to binary files
     *
     * @param object a FlatsyObject with valid URI
     * @param stream an output stream to write content
     * @return null
     */
    public String retrieve(FlatsyObject object, OutputStream stream);

    public <T> Object retrieveAs(FlatsyObject object, Class<T> tClass);

    public void update(FlatsyObject object, FlatsyUpdate update);

    public void delete(FlatsyObject object);

    public List<FlatsyObject> subObjects(FlatsyObject object);
}
