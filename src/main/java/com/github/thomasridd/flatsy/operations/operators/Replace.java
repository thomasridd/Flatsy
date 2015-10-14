package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyObject;

import java.io.IOException;

public class Replace implements FlatsyOperator {
    String original = null;
    String replacement = null;

    public Replace(String original, String replacement) {
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
