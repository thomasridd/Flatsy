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
     * @return the contents of the Flatsy object as a string
     * @throws IOException if the object cannot be opened
     */
    String retrieve(FlatsyObject object) throws IOException;

    /**
     * Writes the content of the object to an OutputStream.
     * This is particularly applicable to binary files
     *
     * @param object a FlatsyObject with valid URI
     * @return an inputstream of the content
     * @throws IOException if the object cannot be opened
     */
    InputStream retrieveStream(FlatsyObject object) throws IOException;

    /**
     * Deserialise the object as a specific class
     *
     * @param object a Flatsy Object
     * @param tClass the class to deserialise the object as
     * @param <T> generic
     * @return The deserialised object
     */
    <T> Object retrieveAs(FlatsyObject object, Class<T> tClass);

    /**
     * Update the object using a FlatsyOperator
     *
     * @param object a FlatsyObject to update
     * @param update the Operator to apply
     */
    void update(FlatsyObject object, FlatsyOperator update);

    /**
     * Delete an object from the system
     *
     * @param object the object
     */
    void delete(FlatsyObject object);

    /**
     * Gets a list of objects that belong to a specified parent node
     *
     * In filesystem terms the folder contents
     *
     * @param object giving the base uri
     * @return a list of objects
     */
    List<FlatsyObject> children(FlatsyObject object);

    /**
     * Gets the parent node of the object
     *
     * Note that the parent of root is null
     *
     * @param object an object
     * @return its folder or equivalent
     */
    FlatsyObject parent(FlatsyObject object);

    /**
     * Move an object to a new uri
     *
     * Note that moving a folder will move all subobjects
     *
     * @param object a flatsy object
     * @param newUri a new uri to move the object to
     */
    void move(FlatsyObject object, String newUri);

    /**
     * Get the mapping that would move an object and all subfiles to a new uri
     *
     * @param object an object that is going to be moved
     * @param newUri the uri it will be moved to
     * @return A map of form moveMap.get(oldUri) = newUri
     */
    Map<String, String> moveMap(FlatsyObject object, String newUri);

    /**
     * The FlatsyObjectType of an object
     *
     * @param uri the id of the object
     * @return uri status
     */
    FlatsyObjectType type(String uri);
}
