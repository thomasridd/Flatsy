package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tom.Ridd on 15/08/15.
 *
 * Matches internally valid JSON Objects
 */
public class ExcelValidXLSX implements FlatsyMatcher {

    @Override
    public boolean matches(FlatsyObject object) {
        if (object.getType() == FlatsyObjectType.Folder) { return false; } // This only applies to files

        try(InputStream stream = object.retrieveStream()) {
            if (!isXLSXValid(stream)) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Use apache poi to check whether a file input stream contains valid xls
     *
     * @param stream
     * @return
     */
    public static boolean isXLSXValid(InputStream stream) {
        try {
            // Open the workbook
            XSSFWorkbook workbook = new XSSFWorkbook(stream);

            // And do something
            workbook.getSheetAt(0);

            return true;
        } catch (Exception e) {

        }
        return false;
    }
}
