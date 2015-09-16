package com.github.thomasridd.flatsy.ons.json;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This cd ..
 *
 * @author david
 */
public class CollectionDescription {

    public String id;
    public String name;
    public Date publishDate;
    public CollectionType type;

    public List<String> inProgressUris;
    public List<String> completeUris;
    public List<String> reviewedUris;
    public boolean approvedStatus;

    /**
     * events related to this collection
     */
    public Events events;

    /**
     * A List of {@link Event} for each uri in the collection.
     */
    public Map<String, Events> eventsByUri;

    /**
     * Default constuructor for serialisation.
     */
    public CollectionDescription() {
        // No action.
    }

    /**
     * Convenience constructor for instantiating with a name.
     *
     * @param name The value for the name.
     */
    public CollectionDescription(String name) {
        this.name = name;
    }

    /**
     * Convenience constructor for instantiating with a name
     * and publish date.
     *
     * @param name
     * @param publishDate
     */
    public CollectionDescription(String name, Date publishDate) {
        this.publishDate = publishDate;
        this.name = name;
    }


    /**
     * Add an event to this collection description.
     * @param event
     */
    public void AddEvent(Event event) {

        if (events == null)
            events = new Events();

        events.add(event);
    }

}
