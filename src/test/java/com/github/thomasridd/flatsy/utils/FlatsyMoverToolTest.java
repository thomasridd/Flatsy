package com.github.thomasridd.flatsy.utils;

import com.github.thomasridd.flatsy.*;
import com.github.thomasridd.flatsy.operations.operators.UriToMap;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.*;

/**
 * Created by thomasridd on 26/08/15.
 */
public class FlatsyMoverToolTest {
    Path root = null;
    FlatsyDatabase db = null;
    Path simpleMapPath = null;
    Path complexMapPath = null;

    @Before
    public void setUp() throws Exception {
        root = Builder.emptyTestDatabase();
        db = new FlatsyFlatFileDatabase(root);


        FlatsyObject chicken = new FlatsyObject("chicken/chicken.json", db);
        FlatsyObject chickenImage = new FlatsyObject("chicken/images/chicken.png", db);
        db.create(chicken, "{'related':['chicken/feuiletes/chickenfeuiletes.json', 'chicken/korma/chickenkorma.json'], 'images': ['chicken/images/chicken.png']}");
        db.create(chickenImage, "image");

        FlatsyObject chickenKorma = new FlatsyObject("chicken/korma/chickenkorma.json", db);
        FlatsyObject chickenKormaImage = new FlatsyObject("chicken/korma/images/chickenKorma.png", db);
        db.create(chickenKorma, "{'related':['chicken/data.json', 'chicken/feuiletes/data.json', 'beef/madras/beefmadras.json'], 'images': ['chicken/korma/images/chickenkorma.png']}");
        db.create(chickenKormaImage, "image");

        FlatsyObject chickenFeuiletes = new FlatsyObject("chicken/feuiletes/chickenfeuiletes.json", db);
        FlatsyObject chickenFeuiletesImage = new FlatsyObject("chicken/feuiletes/images/chickenfeuiletes.png", db);
        db.create(chickenFeuiletes, "{'related':['chicken/chicken.json', 'beef/wellington/beefwellington.json', 'chicken/korma/chickenkorma.json', 'cheese/puff/cheesepuff.json'], 'images': ['chicken/korma/images/chickenfeuiletes.png']}");
        db.create(chickenFeuiletesImage, "image");

        FlatsyObject beef = new FlatsyObject("beef/beef.json", db);
        FlatsyObject beefImage = new FlatsyObject("beef/images/beef.png", db);
        db.create(beef, "{'related':['beef/wellington/beefwellington.json', 'beef/madras/beefmadras.json'], 'images': ['beef/images/beef.png']}");
        db.create(beefImage, "image");

        FlatsyObject beefMadras = new FlatsyObject("beef/madras/beefmadras.json", db);
        FlatsyObject beefMadrasImage = new FlatsyObject("beef/madras/images/beefmadras.png", db);
        db.create(beefMadras, "{'related':['beef/beef.json', 'beef/wellington/beefwellington.json', 'chicken/korma/data.json', 'sag/paneer/sagpaneer.json'], 'images': ['beef/madras/images/beefmadras.png']}");
        db.create(beefMadrasImage, "image");

        FlatsyObject beefWellington = new FlatsyObject("beef/wellington/beefwellington.json", db);
        FlatsyObject beefWellingtonImage1 = new FlatsyObject("beef/wellington/images/beefwellington.png", db);
        db.create(beefWellington, "{'related':['beef/beef.json', 'beef/madras/beefmadras.json', 'chicken/feuiletes/chickenfeuiletes.json', 'cheese/puff/cheesepuff.json'], 'images': ['beef/wellington/images/beefwellington.png']}");
        db.create(beefWellingtonImage1, "beefwellington");

        FlatsyObject cheese = new FlatsyObject("cheese/cheese.json", db);
        FlatsyObject cheeseImage = new FlatsyObject("cheese/images/cheese.png", db);
        db.create(cheese, "{'related':['cheese/puff/cheesepuff.json', 'sag/paneer/sagpaneer.json'], 'images': ['cheese/images/cheese.png']}");
        db.create(cheeseImage, "image");

        FlatsyObject cheesePuff = new FlatsyObject("cheese/puff/cheesepuff.json", db);
        FlatsyObject cheesePuffImage = new FlatsyObject("cheese/puff/images/cheesepuff.png", db);
        db.create(cheesePuff, "{'related':['cheese/cheese.json', 'sag/paneer/sagpaneer.json', 'chicken/feuiletes/chickenfeuiletes.json', 'beef/wellington/beefwellington.json'], 'images': ['cheese/puff/images/cheesepuff.png']}");
        db.create(cheesePuffImage, "image");

        FlatsyObject sagPaneer = new FlatsyObject("sag/paneer/sagpaneer.json", db);
        FlatsyObject sagPaneerImage = new FlatsyObject("sag/paneer/images/sagpaneer.png", db);
        db.create(sagPaneer, "{'related':['cheese/cheese.json', 'cheese/puff/cheesepuff.json', 'beef/madras/beefmadras.json', 'chicken/korma/chickenkorma.json'], 'images': ['sag/paneer/images/sagpaneer.png']}");
        db.create(sagPaneerImage, "image");

        simpleMapPath = Files.createTempFile("simple", ".map");
        complexMapPath = Files.createTempFile("complex", ".map");
        saveMap(simpleMap(), simpleMapPath);
        saveMap(complexMap(), complexMapPath);
    }

    Map<String, String> simpleMap() {
        Map<String, String> map = new HashMap<>();

        map.put("beef", "meat");
        map.put("chicken", "meat");
        map.put("cheese", "vege/dairy");
        map.put("sag", "vege/spinach");

        return map;
    }

    Map<String, String> complexMap() {
        Map<String, String> map = new HashMap<>();

        map.put("beef/wellington", "pastry");
        map.put("chicken/feuiletes", "pastry");
        map.put("cheese/puff", "pastry");

        map.put("chicken/korma", "indian");
        map.put("beef/madras", "indian");
        map.put("sag/paneer", "indian");

        map.put("beef", "ingredients");
        map.put("chicken", "ingredients");
        map.put("cheese", "ingredients");

        return map;
    }

    void saveMap(Map<String, String> map, Path path) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(path.toFile())) {
            for (String key : map.keySet()) {
                out.println(key + "\t" + map.get(key));
            }
        }
    }


    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(root.toFile());

        Files.deleteIfExists(simpleMapPath);
        Files.deleteIfExists(complexMapPath);
    }

    @Test
    public void moverTool_givenMap_shouldInitialise() {
        // Given
        // our database
        Map<String, String> map = new HashMap<>();
        map.put("beef", "meat/beef");
        map.put("chicken", "meat/chicken");
        map.put("cheese", "vegetarian/dairy");
        map.put("sag", "vegetarian/vegetables");

        // When
        // we create a new mover
        FlatsyMoverTool mover = new FlatsyMoverTool(map);

        // Then
        // we expect the tool to be non null
        assertNotNull(mover);
    }

    @Test
    public void moverTool_givenMap_shouldPopulateFromToList() {
        // Given
        // our database
        Map<String, String> map = new HashMap<>();
        map.put("beef", "meat/beef");
        map.put("chicken", "meat/chicken");
        map.put("cheese", "vegetarian/cheese");
        map.put("sag", "vegetarian/spinach");

        // When
        // we create a new mover
        FlatsyMoverTool mover = new FlatsyMoverTool(map);

        // Then
        // we expect the tool to be non null
        assertEquals(4, mover.fromToList.size());
        for (FlatsyMoverTool.FromTo fromTo : mover.fromToList) {
            assertTrue(map.containsKey(fromTo.fromUri));
            assertEquals(map.get(fromTo.fromUri), fromTo.toUri);
        }
    }

    @Test
    public void moverTool_givenPath_shouldInitialise() throws IOException {
        // Given
        // our database
        Path path = simpleMapPath;


        // When
        // we create a new mover
        FlatsyMoverTool mover = new FlatsyMoverTool(path);

        // Then
        // we expect the tool to be non null
        assertNotNull(mover);
    }

    @Test
    public void moverTool_givenPath_shouldPopulateCorrectFromToList() throws IOException {
        // Given
        // our database
        Path path = simpleMapPath;
        Map<String, String> map = simpleMap();

        // When
        // we create a new mover
        FlatsyMoverTool mover = new FlatsyMoverTool(path);

        // Then
        // we expect the tool to be non null
        assertEquals(4, mover.fromToList.size());
        for (FlatsyMoverTool.FromTo fromTo : mover.fromToList) {
            assertTrue(map.containsKey(fromTo.fromUri));
            assertEquals(map.get(fromTo.fromUri), fromTo.toUri);
        }
    }

    @Test
    public void move_givenSimpleMap_shouldMoveFilesToCorrectPlace() throws IOException {
        // Given
        // our database
        Path path = simpleMapPath;
        FlatsyMoverTool mover = new FlatsyMoverTool(path);
        Map<String, String> fullMap = completeMap(simpleMap());

        // When
        // we run the move
        mover.move(db);

        // Then
        // we expect the tool to move all files to their correct place
        for (String key : fullMap.keySet()) {
            FlatsyObject object = new FlatsyObject(fullMap.get(key), db);
            assertNotEquals(FlatsyObjectType.Null, object.getType());
        }
    }


    @Test
    public void move_givenSimpleMap_shouldRemoveFilesFromOldPlace() throws IOException {
        // Given
        // our database
        Path path = simpleMapPath;
        FlatsyMoverTool mover = new FlatsyMoverTool(path);
        Map<String, String> fullMap = completeMap(simpleMap());

        // When
        // we run the move
        mover.move(db);

        // Then
        // we expect the tool to remove all files from their o;d place
        for (String key : fullMap.keySet()) {
            FlatsyObject object = new FlatsyObject(key, db);
            assertEquals(FlatsyObjectType.Null, object.getType());
        }
    }

    @Test
    public void move_givenComplexMap_shouldMoveLongFilesFirst() throws IOException {
        // Given
        // our database
        Path path = complexMapPath;
        FlatsyMoverTool mover = new FlatsyMoverTool(path);

        // When
        // we run the move
        mover.move(db);

        // Then
        // we expect the tool to move all files to their correct place
        assertEquals(FlatsyObjectType.JSONFile, db.get("pastry/beefwellington.json").getType());
        assertEquals(FlatsyObjectType.JSONFile, db.get("ingredients/beef.json").getType());
    }


    @Test
    public void move_givenComplexMap_shouldRemoveFilesFromOldPlace() throws IOException {
        // Given
        // our database
        Path path = complexMapPath;
        FlatsyMoverTool mover = new FlatsyMoverTool(path);
        Map<String, String> fullMap = completeMap(complexMap());

        // When
        // we run the move
        mover.move(db);

        // Then
        // we expect the tool to remove all files from their o;d place
        for (String key : fullMap.keySet()) {
            FlatsyObject object = new FlatsyObject(key, db);
            assertEquals(FlatsyObjectType.Null, object.getType());
        }
    }


    @Test
    public void replace_givenSimpleMap_shouldRemoveReferencesToOldFiles() throws IOException {
        // Given
        // our database
        Path path = simpleMapPath;
        FlatsyMoverTool mover = new FlatsyMoverTool(path);
        Map<String, String> fullMap = completeMap(simpleMap());

        // When
        // we run the move
        mover.move(db);

        // Then
        // we expect the tool to move all files to their correct place
        ConcurrentMap<String, String> map;
        for (String key : fullMap.keySet()) {
            map = new ConcurrentHashMap<>();
            db.root().cursor().query("{uri_contains:.json}").query("{content_contains:" + key + "}").apply(new UriToMap(map, key));
            assertEquals(0, map.size());
        }
    }

    /**
     * Use the db.moveMap() function to get a complete list of all files and folders that need to be moved
     *
     * @param map a map of file moves (including folders)
     * @return the full list of all moves (including files, folders)
     */
    Map<String, String> completeMap(Map<String, String> map) {
        Map<String, String> fullMap = new HashMap<>();
        for (String key : map.keySet()) {
            Map<String, String> moveMap = db.moveMap(new FlatsyObject(key, db), map.get(key));
            for (String subkey : moveMap.keySet()) {
                fullMap.put(subkey, moveMap.get(subkey));
            }
        }
        return fullMap;
    }
}