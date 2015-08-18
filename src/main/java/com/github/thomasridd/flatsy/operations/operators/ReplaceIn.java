package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyObject;

import java.io.IOException;

/**
 * Created by thomasridd on 18/08/15.
 */
public class ReplaceIn implements FlatsyOperator {
    String original = null;
    String replacement = null;

    public ReplaceIn(String original, String replacement) {
        this.original = original;
        this.replacement = replacement;
    }


    @Override
    public void apply(FlatsyObject object) {
        try {
            String content = object.retrieve();
            content = content.replaceAll(original, replacement);
            object.create(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
