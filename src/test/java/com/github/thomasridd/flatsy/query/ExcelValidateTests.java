package com.github.thomasridd.flatsy.query;

import com.github.thomasridd.flatsy.Builder;
import com.github.thomasridd.flatsy.FlatsyFlatFileDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.operations.operators.UriToOutput;
import com.github.thomasridd.flatsy.query.matchers.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Created by thomasridd on 21/10/15.
 */
public class ExcelValidateTests {

    @Test
    public void excelXlsValidator_givenValidExcel_returnsTrue() throws IOException {
        // Given
        // a item that is excel and a matcher
        Path dbPath = Builder.testDatabaseWithResources("exceltest");
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(dbPath);

        FlatsyObject isExcel = db.get("example.xls");
        FlatsyMatcher matcher = new ExcelValidXLS();


        // When
        // we run the matcher
        boolean doesMatch = matcher.matches(isExcel);

        // Then
        // it returns true
        assertEquals(true, doesMatch);
    }

    @Test
    public void excelXlsValidator_givenInvalidExcel_returnsFalse() throws IOException {
        // Given
        // a item that is not excel and a matcher
        Path dbPath = Builder.testDatabaseWithResources("exceltest");
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(dbPath);

        FlatsyObject isNotExcel = db.get("text.xls");
        FlatsyMatcher matcher = new ExcelValidXLS();


        // When
        // we run the matcher
        boolean doesMatch = matcher.matches(isNotExcel);

        // Then
        // it returns false
        assertEquals(false, doesMatch);
    }

    @Test
    public void excelXlsValidator_givenTruncatedExcel_returnsFalse() throws IOException {
        // Given
        // a item that is a truncated excel file and a matcher
        Path dbPath = Builder.testDatabaseWithResources("exceltest");
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(dbPath);

        FlatsyObject truncated = db.get("truncated.xls");
        FlatsyMatcher matcher = new ExcelValidXLS();


        // When
        // we run the matcher
        boolean doesMatch = matcher.matches(truncated);

        // Then
        // it returns false
        assertEquals(false, doesMatch);
    }



    @Test
    public void excelXlsXValidator_givenValidExcel_returnsTrue() throws IOException {
        // Given
        // a item that is excel and a matcher
        Path dbPath = Builder.testDatabaseWithResources("exceltest");
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(dbPath);

        FlatsyObject isExcel = db.get("example.xlsx");
        FlatsyMatcher matcher = new ExcelValidXLSX();


        // When
        // we run the matcher
        boolean doesMatch = matcher.matches(isExcel);

        // Then
        // it returns true
        assertEquals(true, doesMatch);
    }

    @Test
    public void excelXlsXValidator_givenInvalidExcel_returnsFalse() throws IOException {
        // Given
        // a item that is not excel and a matcher
        Path dbPath = Builder.testDatabaseWithResources("exceltest");
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(dbPath);

        FlatsyObject isNotExcel = db.get("text.xlsx");
        FlatsyMatcher matcher = new ExcelValidXLSX();


        // When
        // we run the matcher
        boolean doesMatch = matcher.matches(isNotExcel);

        // Then
        // it returns false
        assertEquals(false, doesMatch);
    }

    @Test
    public void excelXlsXValidator_givenTruncatedExcel_returnsFalse() throws IOException {
        // Given
        // a item that is a truncated excel file and a matcher
        Path dbPath = Builder.testDatabaseWithResources("exceltest");
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(dbPath);

        FlatsyObject truncated = db.get("truncated.xlsx");
        FlatsyMatcher matcher = new ExcelValidXLSX();


        // When
        // we run the matcher
        boolean doesMatch = matcher.matches(truncated);

        // Then
        // it returns false
        assertEquals(false, doesMatch);
    }

    public void excelXlsValidator_validateExcelFiles() throws IOException {
        // Given
        // a item that is a truncated excel file and a matcher
        FlatsyFlatFileDatabase db = new FlatsyFlatFileDatabase(Paths.get("/Users/thomasridd/Documents/onswebsite/zebedee/master"));

        FlatsyMatcher files = new IsFile();
        FlatsyMatcher endsXLS = new UriContains(".xls");
        FlatsyMatcher invalid = new Not(new ExcelValid());


        // When
        // we run the matcher
        db.root().query(files).query(endsXLS).query(invalid).apply(new UriToOutput(System.out));

    }
}
