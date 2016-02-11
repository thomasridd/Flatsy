package com.github.thomasridd.flatsy.util;

import com.github.thomasridd.flatsy.Builder;
import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by thomasridd on 20/10/15.
 */
public class FlatsyUtilTest {
    @Test
    public void commandLineParser_givenMixedLine_returnsAppropriateArguments() {
        // Given
        // a command line with a mixture of arguments
        String command = "\"Flat 4\" 86 Greencroft  gardens";

        // When
        // it is parsed
        List<String> strings = FlatsyUtil.commandArguments(command);

        // Then
        // we get appropriate arguments returned
        assertEquals("Flat 4", strings.get(0));
        assertEquals("86", strings.get(1));
        assertEquals("Greencroft", strings.get(2));
        assertEquals("gardens", strings.get(3));
    }

    @Test
    public void commandLineParser_givenDoubleQuotedLine_returnsAppropriateArguments() {
        // Given
        // a command line with a mixture of arguments
        String command = "\"\"TRUE\"\" true";

        // When
        // it is parsed
        List<String> strings = FlatsyUtil.commandArguments(command);

        // Then
        // we get appropriate arguments returned
        assertEquals("\"TRUE\"", strings.get(0));
        assertEquals("true", strings.get(1));
    }

    @Test
    public void stringBuilder_givenSimpleExpression_returnsExpectedResult() throws IOException {
        // Given
        // a simple expression
        String expression = "Black + berry";

        // When
        // we build a string
        String result = FlatsyUtil.stringExpression(expression, null);

        // Then
        // it returns the expected result
        assertEquals("Blackberry", result);
    }

    @Test
    public void stringBuilder_withDoublePlus_addsSingleSpace() throws IOException {
        // Given
        // a simple expression
        String expression = "Black ++ berry";

        // When
        // we build a string
        String result = FlatsyUtil.stringExpression(expression, null);

        // Then
        // it returns the expected result
        assertEquals("Black berry", result);
    }

    @Test
    public void stringBuilder_withFlatsyObject_addsUri() throws IOException {
        // Given
        // a flatsy object and simple expression
        String expression = "URI= + ~.uri";
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Builder.emptyTestDatabase());
        FlatsyObject object = new FlatsyObject("test/object", db);

        // When
        // we build a string
        String result = FlatsyUtil.stringExpression(expression, object);

        // Then
        // it returns the expected result
        assertEquals("URI=test/object", result);
    }

    @Test
    public void stringBuilder_withFlatsyObject_addsParent() throws IOException {
        // Given
        // a flatsy object and simple expression
        String expression = "Parent= + ~.parent";
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Builder.emptyTestDatabase());
        FlatsyObject object = new FlatsyObject("theme/parent/object.json", db);

        // When
        // we build a string
        String result = FlatsyUtil.stringExpression(expression, object);

        // Then
        // it returns the expected result
        assertEquals("Parent=theme/parent", result);
    }

    @Test
    public void stringBuilder_withFlatsyObject_addsFilename() throws IOException {
        // Given
        // a flatsy object and simple expression
        String expression = "File= + ~.file";
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Builder.emptyTestDatabase());
        FlatsyObject object = new FlatsyObject("theme/parent/object.json", db);
        object.create("Test");

        // When
        // we build a string
        String result = FlatsyUtil.stringExpression(expression, object);

        // Then
        // it returns the expected result
        assertEquals("File=object.json", result);
    }

    @Test
    public void stringBuilder_withJSONObject_addsJSONPath() throws IOException {
        // Given
        // a simple expression
        String expression = "Title: ++ $.description.title";
        FlatsyDatabase db = new FlatsyFlatFileDatabase(Builder.copyFlatFiles());

        FlatsyObject object = new FlatsyObject("births/data.json", db);

        // When
        // we build a string
        String result = FlatsyUtil.stringExpression(expression, object);

        // Then
        // it returns the expected result
        assertEquals("Title: Births, deaths and marriages", result);
    }
}