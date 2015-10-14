package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyObject;

import java.io.IOException;
import java.io.OutputStream;

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
public class UriToOutput implements FlatsyOperator {
    OutputStream stream;

    public UriToOutput(OutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void apply(FlatsyObject object) {
        try {
            stream.write((object.uri.toString() + "\n").getBytes());
        } catch (IOException e) {
            System.out.println("Failed to print for uri: " + object.uri);
        }
    }
}
