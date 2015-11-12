package com.github.thomasridd.flatsy.operations;

import com.github.thomasridd.flatsy.operations.operators.FlatsyOperator;
import com.github.thomasridd.flatsy.query.FlatsyCursor;

/**
 * Created by Tom.Ridd on 15/08/15.
 *
 * Worker applies an operation to all members of a collection
 *
 */
public class FlatsyWorker {
    FlatsyOperator operation;

    /**
     * Apply an operation to all objects identified by a cursor
     *
     * @param operation an operation for this worker to apply
     */
    public FlatsyWorker(FlatsyOperator operation) {
        this.operation = operation;
    }

    /**
     * Apply the workers operation to all objects defined by a cursor
     *
     * @param cursor a cursor
     */
    public void updateAll(FlatsyCursor cursor) {
        while(cursor.next()) {
            this.operation.apply(cursor.currentObject());
        }
    }
}
