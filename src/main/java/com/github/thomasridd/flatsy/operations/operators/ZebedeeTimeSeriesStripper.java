package com.github.thomasridd.flatsy.operations.operators;

import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by thomasridd on 18/08/15.
 */
public class ZebedeeTimeSeriesStripper implements FlatsyOperator {
    FlatsyDatabase db;

    public ZebedeeTimeSeriesStripper(FlatsyDatabase db) {
        this.db = db;
    }

    @Override
    public void apply(FlatsyObject object) {
        try {
            try(InputStream stream = object.retrieveStream()){
                TimeSeries series = (TimeSeries) ContentUtil.deserialisePage(stream);
                series.years = new TreeSet<>();
                series.months = new TreeSet<>();
                series.quarters = new TreeSet<>();
                series.sourceDatasets = new ArrayList<>();

                String content = ContentUtil.serialise(series);
                if (db != null) {
                    db.create(object, content);
                } else {
                    object.create(content);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
