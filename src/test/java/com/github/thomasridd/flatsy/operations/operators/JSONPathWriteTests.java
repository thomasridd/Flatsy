package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.Builder;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.query.matchers.JSONPathEquals;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * Created by thomasridd on 21/10/15.
 */
public class JSONPathWriteTests {

    @Test
    public void putOperator_givenSimplePath_addsField() throws IOException {
        // Given
        // an empty database with a proto-json item
        Path dbPath = Builder.emptyTestDatabase();
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(dbPath);
        FlatsyObject object = new FlatsyObject("put/test.json", db);
        object.create("{}");

        // When
        // we write a simple object
        JSONPathPut put = new JSONPathPut("$", "type", "animal");
        put.apply(object);

        // Then
        // it creates a field
        assertEquals("{\"type\":\"animal\"}", object.retrieve());
    }

    @Test
    public void putOperator_onRepeat_setsField() throws IOException {
        // Given
        // a database with a single item
        Path dbPath = Builder.emptyTestDatabase();
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(dbPath);
        FlatsyObject object = new FlatsyObject("put/test.json", db);
        object.create("{}");
        JSONPathPut put = new JSONPathPut("$", "type", "animal");
        put.apply(object);

        // When
        // we write over that property object
        JSONPathPut putAgain = new JSONPathPut("$", "type", "mammal");
        putAgain.apply(object);

        // Then
        // it updates the field
        assertEquals("{\"type\":\"mammal\"}", object.retrieve());
    }

    @Test
    public void putOperator_withJsonArray_setsArray() throws IOException {
        // Given
        // a database with a single item
        Path dbPath = Builder.emptyTestDatabase();
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(dbPath);
        FlatsyObject object = new FlatsyObject("put/test.json", db);
        object.create("{}");


        // When
        // we write over that property object
        JSONPathPut put = new JSONPathPut("$", "types", "[\"animal\",\"vegetable\",\"mineral\"]");
        put.apply(object);

        // Then
        // it updates the field
        assertEquals("{\"types\":[\"animal\",\"vegetable\",\"mineral\"]}", object.retrieve());
    }

    @Test
    public void putOperator_withJsonObject_setsObject() throws IOException {
        // Given
        // a database with a single item
        Path dbPath = Builder.emptyTestDatabase();
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(dbPath);
        FlatsyObject object = new FlatsyObject("put/test.json", db);
        object.create("{}");


        // When
        // we write over that property object
        JSONPathPut put = new JSONPathPut("$", "animal", "{\"name\":\"horse\",\"feet\":4}");
        put.apply(object);

        // Then
        // it updates the field
        System.out.println(object.retrieve());
        assertEquals("{\"animal\":{\"name\":\"horse\",\"feet\":4}}", object.retrieve());
    }

    @Test
    public void addOperator_givenString_addsValue() throws IOException {
        // Given
        // an empty database with a proto-json item
        Path dbPath = Builder.emptyTestDatabase();
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(dbPath);
        FlatsyObject object = new FlatsyObject("put/test.json", db);
        object.create("{\"types\":[\"animal\",\"vegetable\",\"mineral\"]}");

        // When
        // we write a simple object
        JSONPathAdd add = new JSONPathAdd("$", "types", "robot");
        add.apply(object);

        // Then
        // it creates a field
        System.out.println(object.retrieve());
        assertEquals("{\"types\":[\"animal\",\"vegetable\",\"mineral\",\"robot\"]}", object.retrieve());
    }

    @Test
    public void addOperator_whenArrayDoesntExist_createsArray() throws IOException {
        // Given
        // an empty database with a proto-json item
        Path dbPath = Builder.emptyTestDatabase();
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(dbPath);
        FlatsyObject object = new FlatsyObject("put/test.json", db);
        object.create("{}");

        // When
        // we write a simple object
        JSONPathAdd add = new JSONPathAdd("$", "types", "robot");
        add.apply(object);

        // Then
        // it creates a field
        System.out.println(object.retrieve());
        assertEquals("{\"types\":[\"robot\"]}", object.retrieve());
    }

    @Test
    public void addOperator_givenArray_addsSubArray() throws IOException {
        // Given
        // an empty database with a proto-json item
        Path dbPath = Builder.emptyTestDatabase();
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(dbPath);
        FlatsyObject object = new FlatsyObject("put/test.json", db);
        object.create("{\"types\":[\"animal\"]}");



        // When
        // we write a simple object
        JSONPathAdd add = new JSONPathAdd("$", "types", "[\"one\",\"two\",\"three\"]");
        add.apply(object);

        // Then
        // it creates a field
        System.out.println(object.retrieve());
        assertEquals("{\"types\":[\"animal\",[\"one\",\"two\",\"three\"]]}", object.retrieve());
    }
}