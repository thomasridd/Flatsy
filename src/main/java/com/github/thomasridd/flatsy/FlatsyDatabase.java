package com.github.thomasridd.flatsy;

import com.github.thomasridd.flatsy.operations.operators.FlatsyOperator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
    FlatsyObject root();

    FlatsyObject get(String uri);

    void create(FlatsyObject object, String content);

    void create(FlatsyObject object, InputStream content) throws IOException;

    /**
     * Retrieves the string contents for the FlatsyObject database object
     * Suitable for deserialising objects from JSON
     *
     * @param object a FlatsyObject with valid URI
     * @return
     */
    String retrieve(FlatsyObject object) throws IOException;

    /**
     * Writes the content of the object to an OutputStream.
     * This is particularly applicable to binary files
     *
     * @param object a FlatsyObject with valid URI
     * @return an inputstream of the content
     */
    InputStream retrieveStream(FlatsyObject object) throws IOException;

    <T> Object retrieveAs(FlatsyObject object, Class<T> tClass);

    void update(FlatsyObject object, FlatsyOperator update);

    void delete(FlatsyObject object);

    /**
     * Gets a list of objects that belong to a specified parent node
     *
     * In filesystem terms the folder contents
     *
     * @param object giving the base uri
     * @return
     */
    List<FlatsyObject> children(FlatsyObject object);


    void move(FlatsyObject object, String newUri);

    Map<String, String> moveMap(FlatsyObject object, String newUri);

    FlatsyObjectType type(String uri);
}
