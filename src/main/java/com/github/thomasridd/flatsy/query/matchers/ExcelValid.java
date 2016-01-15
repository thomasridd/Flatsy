package com.github.thomasridd.flatsy.query.matchers;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tom.Ridd on 15/08/15.
 *
 * Matches internally valid JSON Objects
 */
public class ExcelValid implements FlatsyMatcher {

    private ExcelValidXLS excelValidXLS = new ExcelValidXLS();
    private ExcelValidXLSX excelValidXLSX = new ExcelValidXLSX();

    @Override
    public boolean matches(FlatsyObject object) {
        return excelValidXLS.matches(object) || excelValidXLSX.matches(object);
    }
}
