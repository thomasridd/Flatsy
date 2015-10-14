package com.github.thomasridd.flatsy.operations.operators;

import com.github.thomasridd.flatsy.FlatsyObject;
import com.github.thomasridd.flatsy.http.Endpoint;
import com.github.thomasridd.flatsy.http.Http;
import com.github.thomasridd.flatsy.http.Response;
import com.github.thomasridd.flatsy.ons.ZebedeeHost;
import com.github.thomasridd.flatsy.ons.json.CollectionDescription;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
public class ZebedeeComplete implements FlatsyOperator {
    CollectionDescription collectionDescription;

    public ZebedeeComplete(CollectionDescription collectionDescription) {
        this.collectionDescription = collectionDescription;
    }

    @Override
    public void apply(FlatsyObject object) {
        try {
            Path path = Files.createTempFile("FlatsyObject", ".obj");
            try (InputStream input = object.retrieveStream(); OutputStream output = Files.newOutputStream(path) ){
                IOUtils.copy(input, output);
            }

            complete(collectionDescription.id, object.uri, ZebedeeHost.httpPublisher);
        } catch (IOException e) {
            System.out.println("Failed to upload for uri: " + object.uri);
        }
    }

    public static Response<String> complete(String collectionName, String uri, Http http) throws IOException {
        Endpoint contentEndpoint = ZebedeeHost.complete.addPathSegment(collectionName).setParameter("uri", uri);
        return http.post(contentEndpoint, "", String.class);
    }
}
