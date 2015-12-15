package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyDatabase;
import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.FlatsyObjectType;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasridd on 18/08/15.
 *
 * Migrate
 *
 * Copies the object and contents to a second FlatsyDatabase instance
 *
 * Use for backup, partial backup, or
 *
 */
public class Delete implements FlatsyOperator {

    /**
     * Create copy object to an alternate database
     *
     */
    public Delete() {
    }

    /**
     * Apply the copy operation
     *
     * @param object to a specific object
     */
    @Override
    public void apply(FlatsyObject object) {
        if (object.getType() == FlatsyObjectType.Null) { return; }

        object.db.delete(object);
        deleteEmptyParents(object);
    }

    private void deleteEmptyParents(FlatsyObject object) {
        FlatsyObject folder = object.parent();
        if (folder.children().size() == 0) {
            folder.db.delete(folder);
            deleteEmptyParents(folder) ;
        };
    }
}
