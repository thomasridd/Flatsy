package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.update.FlatsyUpdate;

import java.io.IOException;
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
    public void create(FlatsyObject object, InputStream content) throws IOException;

    /**
     * Retrieves the string contents for the FlatsyObject database object
     * Suitable for deserialising objects from JSON
     *
     * @param object a FlatsyObject with valid URI
     * @return
     */
    public String retrieve(FlatsyObject object) throws IOException;

    /**
     * Writes the content of the object to an OutputStream.
     * This is particularly applicable to binary files
     *
     * @param object a FlatsyObject with valid URI
     * @return an inputstream of the content
     */
    public InputStream retrieveStream(FlatsyObject object) throws IOException;

    public <T> Object retrieveAs(FlatsyObject object, Class<T> tClass);

    public void update(FlatsyObject object, FlatsyUpdate update);

    public void delete(FlatsyObject object);

    /**
     * Gets a list of objects that belong to a specified parent node
     *
     * In filesystem terms the folder contents
     *
     * @param object giving the base uri
     * @return
     */
    public List<FlatsyObject> children(FlatsyObject object);



    public FlatsyObjectType type(String uri);
}
