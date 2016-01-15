package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;
import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tom.Ridd on 15/08/15.
 *
 * Matches internally valid JSON Objects
 */
public class ExcelValidXLS implements FlatsyMatcher {

    @Override
    public boolean matches(FlatsyObject object) {
        if (object.getType() == FlatsyObjectType.Folder) { return false; } // This only applies to files

        try(InputStream stream = object.retrieveStream()) {
            if (!isXLSValid(stream)) {
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
    public static boolean isXLSValid(InputStream stream) {
        try {
            // Open the workbook
            HSSFWorkbook workbook = new HSSFWorkbook(stream);

            // And do something
            workbook.getSheetAt(0);

            return true;
        } catch (org.apache.poi.hssf.OldExcelFormatException e) {
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }
}
