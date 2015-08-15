package com.github.thomasridd.flatsy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import static org.junit.Assert.*;

public class FlatsyFlatFileDatabaseTest {
Path root = null;

    @Before
    public void setUp() throws Exception {
        // For all tests we copy the flatFileTest example dataset

        root = Builder.copyFlatFiles();
    }

    @After
    public void tearDown() throws Exception {
        // Garbage collection (try to avoid choking the file system)

        FileUtils.deleteDirectory(root.toFile());
    }

    @Test
    public void flatFileDatabase_onInitialise_shouldNotBeNull() {
       // Given
        // a folder
        Path folder = root;

        // When
        // we create a database
        FlatsyDatabase db = new FlatsyFlatFileDatabase(folder);

        // Then
        // we expect initialised object
        assertNotNull(db);
    }
    @Test
    public void rootObject_onInitialise_shouldGiveBlankURI() {
        // Given
        // a folder
        Path folder = root;

        // When
        // we create a database
        FlatsyDatabase db = new FlatsyFlatFileDatabase(folder);

        // Then
        // we expect initialised object
        assertNotNull(db.rootObject());
        assertEquals("", db.rootObject().uri);
    }

    @Test
    public void key_forExistingJsonURI_shouldGiveJSONFile() {
        // Given
        // key

        // When
        // forExistingJsonURI

        // Then
        // shouldGiveJSONFile
    }

    @Test
    public void retrieve_forValidURI_shouldReturnFlatsyObject() throws IOException {
        // Given
        // a database
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        // When
        // we retrieve a valid uri
        FlatsyObject object = new FlatsyObject("births/data.json", db);
        String validString = db.retrieve(object);

        // Then
        // we expect to have file content
        assertNotNull(validString);
    }
    @Test
    public void retrieve_forInvalidURI_shouldReturnNull() throws IOException {
        // Given
        // a database
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        // When
        // we retrieve a valid uri
        FlatsyObject object = new FlatsyObject("bingy/bangy/bong.json", db);
        String invalidString = db.retrieve(object);

        // Then
        // we expect to have file content
        assertNull(invalidString);
    }

    @Test
    public void create_newContentForExistingFolder_shouldCreateFile() throws IOException {
        // Given
        // we create some content
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        String newContent = "This is new content";
        String newURI = "births/foo.json";

        // When
        // we create the file
        FlatsyObject object = new FlatsyObject(newURI, db);
        db.create(object, newContent);

        // Then
        // when we retrieve the file it has the content
        assertEquals(newContent, db.retrieve(new FlatsyObject(newURI, db)));
    }
    @Test
    public void create_forExistingContent_shouldOverwrite() throws IOException {
        // Given
        // we create some content
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        String content = "This is original content";
        String uri = "births/original.json";
        db.create(new FlatsyObject(uri, db), content);

        // When
        // we create new content
        String newContent = "This is new content";
        db.create(new FlatsyObject(uri, db), content);


        // Then
        // when we retrieve the file it has the content
        assertEquals(newContent, db.retrieve(new FlatsyObject(uri, db)));
    }
    @Test
    public void create_newContentForNonExistingFolder_shouldCreateStructure() throws IOException {
        // Given
        // we create some content
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        String newContent = "This is new content";
        String newURI = "births/flatsyTest/foo.json";
        String newFolderURI = "births/flatsyTest";

        // When
        // we create the file
        FlatsyObject object = new FlatsyObject(newURI, db);
        db.create(object, newContent);

        // Then
        // files and folders exist and when we retrieve the file it has the content
        assertEquals(FlatsyObjectType.JSONFile, db.key(newURI));
        assertEquals(FlatsyObjectType.Folder, db.key(newFolderURI));
        assertEquals(newContent, db.retrieve(new FlatsyObject(newURI, db)));

    }

    @Test
    public void update_withRenameUpdate_shouldRenameFile() {
        //TODO
    }

    @Test
    public void delete_withExistingFile_shouldDeleteFile() {
        // Given
        // we create some content
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        String content = "This is original content";
        String uri = "births/original.json";

        db.create(new FlatsyObject(uri, db), content);
        assertEquals(FlatsyObjectType.JSONFile, db.key(uri).getType());


        // When
        // we delete the
        db.delete(new FlatsyObject(uri, db));

        // Then
        // when there is no file for our key
        assertEquals(FlatsyObjectType.Null, db.key(uri).getType());
    }
}