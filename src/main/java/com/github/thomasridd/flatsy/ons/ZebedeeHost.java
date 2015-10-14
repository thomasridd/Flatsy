package com.github.thomasridd.flatsy.ons;


import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.thomasridd.flatsy.http.*;
import com.github.thomasridd.flatsy.ons.json.CollectionDescription;
import com.github.thomasridd.flatsy.ons.json.CollectionType;
import com.github.thomasridd.flatsy.ons.json.Credentials;
import com.github.thomasridd.flatsy.ons.json.serialiser.IsoDateSerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Date;

public class ZebedeeHost {

    public static final Host zebedeeHost = new Host(StringUtils.defaultIfBlank(getValue("ZEBEDEE_HOST"), "http://localhost:8082"));

    public static final Endpoint login = new Endpoint(zebedeeHost, "login");
    public static final Endpoint users = new Endpoint(zebedeeHost, "users");
    public static final Endpoint password = new Endpoint(zebedeeHost, "password");
    public static final Endpoint permission = new Endpoint(zebedeeHost, "permission");
    public static final Endpoint approve = new Endpoint(zebedeeHost, "approve");
    public static final Endpoint collections = new Endpoint(zebedeeHost, "collections");
    public static final Endpoint collection = new Endpoint(zebedeeHost, "collection");
    public static final Endpoint content = new Endpoint(zebedeeHost, "content");
    public static final Endpoint transfer = new Endpoint(zebedeeHost, "transfer");
    public static final Endpoint browse = new Endpoint(zebedeeHost, "browse");
    public static final Endpoint complete = new Endpoint(zebedeeHost, "complete");
    public static final Endpoint review = new Endpoint(zebedeeHost, "review");
    public static final Endpoint teams = new Endpoint(zebedeeHost, "teams");
    public static final Endpoint dataservices = new Endpoint(zebedeeHost, "dataservices");
    public static final Endpoint cleanup = new Endpoint(zebedeeHost, "cleanup");
    public static final Endpoint publish = new Endpoint(zebedeeHost, "publish");
    public static final Endpoint collectionBrowseTree = new Endpoint(zebedeeHost, "collectionBrowseTree");
    public static final Endpoint collectionDetails = new Endpoint(zebedeeHost, "collectionDetails");


    public static Http httpAdministrator;
    public static Http httpPublisher;
    public static Http httpSecondSetOfEyes;
    private static String tokenAdministrator;
    private static String tokenPublisher;
    private static String tokenSecondSetOfEyes;

    public static void initialiseZebedeeConnection() throws IOException {
        Serialiser.getBuilder().registerTypeAdapter(Date.class, new IsoDateSerializer());

        SetupBeforeTesting setup = new SetupBeforeTesting();
        try {
            setup.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        httpAdministrator = Sessions.get("administrator");
        httpPublisher = Sessions.get("publisher");
        httpSecondSetOfEyes = Sessions.get("secondSetOfEyes");

        Credentials credentials = SetupBeforeTesting.adminCredentials;
        Response<String> response = httpAdministrator.post(ZebedeeHost.login, credentials, String.class);
        tokenAdministrator = response.body;

        credentials = SetupBeforeTesting.publisherCredentials;
        response = httpPublisher.post(ZebedeeHost.login, credentials, String.class);
        tokenPublisher = response.body;

        credentials = SetupBeforeTesting.secondSetOfEyesCredentials;
        response = httpSecondSetOfEyes.post(ZebedeeHost.login, credentials, String.class);
        tokenSecondSetOfEyes = response.body;

        httpAdministrator.addHeader("x-florence-token", tokenAdministrator);
        httpPublisher.addHeader("x-florence-token", tokenPublisher);
        httpSecondSetOfEyes.addHeader("x-florence-token", tokenSecondSetOfEyes);
    }

    /**
     * Creates a collection with no content
     *
     * @return The collection's {@link CollectionDescription}
     * @throws IOException
     */
    public static CollectionDescription publishedCollection(String name) throws IOException {
        CollectionDescription collection = createCollectionDescription(name);
        return postCollection(collection, httpPublisher).body;
    }
    public static CollectionDescription createCollectionDescription(String name) {
        CollectionDescription collection = new CollectionDescription();
        collection.name = "Flatsy_" + name + "_" + (new Date()).toString();
        collection.publishDate = new Date();
        collection.type = CollectionType.manual;
        return collection;
    }

    private static String getValue(String key) {
        return StringUtils.defaultIfBlank(System.getProperty(key), System.getenv(key));
    }

    public static Response<CollectionDescription> postCollection(CollectionDescription collection, Http http) throws IOException {
        return http.post(ZebedeeHost.collection, collection, CollectionDescription.class);
    }

    public static Response<String> approve(String collectionID) throws IOException {
        Endpoint endpoint = ZebedeeHost.approve.addPathSegment(collectionID);
        return httpPublisher.post(endpoint, null, String.class);
    }

    public static Response<String> publish(String collectionID) throws IOException {
        Endpoint endpoint = ZebedeeHost.publish.addPathSegment(collectionID);
        return httpPublisher.post(endpoint, null, String.class);
    }
}
