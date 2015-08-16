package com.github.thomasridd.flatsy;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

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
    public void type_forExistingJsonURI_shouldGiveJSONFile() {
        // Given
        // setup with json file
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        String jsonFile = "births/data.json";

        // When
        // we get the type
        FlatsyObjectType type = db.type(jsonFile);

        // Then
        // shouldGiveJSONFile
        assertEquals(FlatsyObjectType.JSONFile, type);
    }
    @Test
    public void type_forExistingNonJsonURI_shouldGiveOtherFile() {
        // Given
        // setup with json file
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        String otherFile = "births/adoption/bulletins/englandandwales/2013-08-20/283368d3.html";

        // When
        // we get the type
        FlatsyObjectType type = db.type(otherFile);

        // Then
        // shouldGiveJSONFile
        assertEquals(FlatsyObjectType.OtherFile, type);
    }
    @Test
    public void type_forFolder_shouldGiveFolder() {
        // Given
        // setup with json file
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        String folder = "births";

        // When
        // we get the type
        FlatsyObjectType type = db.type(folder);

        // Then
        // should give folder
        assertEquals(FlatsyObjectType.Folder, type);
    }
    @Test
    public void type_forMissingFile_shouldGiveObjectTypeNull() {
        // Given
        // setup with rubbish filename
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        String uri = "births/foo.xml";

        // When
        // we get the type
        FlatsyObjectType type = db.type(uri);

        // Then
        // should give null
        assertEquals(FlatsyObjectType.Null, type);
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
    public void retrieveFromStream_givenFile_shouldReturnStreamForRetrieve() throws IOException {
        // Given
        // a database
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        // When
        // we retrieve a valid uri
        String retrieved = null;
        FlatsyObject object = new FlatsyObject("births/data.json", db);

        try(InputStream stream = db.retrieveStream(object)) {
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer, "UTF8");
            retrieved = writer.toString();
        }

        // Then
        // we compare to regular (and already tested) retrieve
        assertEquals(db.retrieve(object), retrieved);
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
        db.create(new FlatsyObject(uri, db), newContent);


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
        assertEquals(FlatsyObjectType.JSONFile, db.type(newURI));
        assertEquals(FlatsyObjectType.Folder, db.type(newFolderURI));
        assertEquals(newContent, db.retrieve(new FlatsyObject(newURI, db)));
    }
    @Test
    public void createWithStream_forNewContent_shouldCreateContent() throws IOException {
        // Given
        // we create some content
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);
        String uri = "births/data.json";
        String uriNew = "births/new.json";

        // When
        // we copy the file
        try(InputStream stream = Files.newInputStream(root.resolve(uri))) {
            db.create(new FlatsyObject(uriNew, db), stream);
        }

        // Then
        // files and folders exist and when we retrieve the file it has the content
        assertEquals(db.retrieve(new FlatsyObject(uri, db)), db.retrieve(new FlatsyObject(uriNew, db)));
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
        assertEquals(FlatsyObjectType.JSONFile, db.type(uri));

        // When
        // we delete the
        db.delete(new FlatsyObject(uri, db));

        // Then
        // when there is no file for our key
        assertEquals(FlatsyObjectType.Null, db.type(uri));
    }

    @Test
    public void childList_givenAFolder_shouldGiveListOfFolderContents() {
        // Given
        // A folder with some content
        FlatsyDatabase db = new FlatsyFlatFileDatabase(root);

        FlatsyObject folder = new FlatsyObject("test", db);
        db.create(new FlatsyObject("test/file.json", db), "testing");
        db.create(new FlatsyObject("test/folder/file.json", db), "testing");

        assertEquals(FlatsyObjectType.Folder, folder.getType());

        // When
        // We get a list of subobjects
        List<FlatsyObject> flatsyObjects = db.children(folder);

        // Then
        // It should contain contents of that folder
        Collections.sort(flatsyObjects);
        assertEquals(2, flatsyObjects.size());
        assertEquals("test/file.json", flatsyObjects.get(0).uri);
        assertEquals("test/folder", flatsyObjects.get(1).uri);
    }
}