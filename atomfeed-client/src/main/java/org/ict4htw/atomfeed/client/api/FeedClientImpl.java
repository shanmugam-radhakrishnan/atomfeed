package org.ict4htw.atomfeed.client.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.ict4htw.atomfeed.client.AtomFeedClientException;
import org.ict4htw.atomfeed.client.FeedEnumerator;
import org.ict4htw.atomfeed.client.api.data.Event;
import org.ict4htw.atomfeed.client.domain.Marker;
import org.ict4htw.atomfeed.client.repository.AllFeeds;
import org.ict4htw.atomfeed.client.repository.AllMarkers;

import com.sun.syndication.feed.atom.Entry;

public class FeedClientImpl implements FeedClient {
    private static final String ATOM_MEDIA_TYPE = "application/atom+xml";
    private AllFeeds allFeeds;
    private AllMarkers allMarkers;

    //@Autowired
    public FeedClientImpl(AllFeeds allFeeds, AllMarkers allMarkers) {
        this.allFeeds = allFeeds;
        this.allMarkers = allMarkers;
    }

    @Override
    public List<Event> unprocessedEvents(URI feedUri) {
        try {
            Marker marker = allMarkers.get(feedUri);
            FeedEnumerator feedEnumerator = new FeedEnumerator(allFeeds, feedUri);
            List<Entry> entries = (marker != null) ? feedEnumerator.newerEntries(marker.getEntryId()) : feedEnumerator.getAllEntries();
            ArrayList<Event> events = new ArrayList<Event>();
            for (Entry entry : entries) {
                events.add(new Event(entry));
            }
            
            return events;
        } catch (URISyntaxException e) {
            throw new AtomFeedClientException(e);
        }
    }

    @Override
    public void confirmProcessed(URI feedUri, String feedEntryId) {
        allMarkers.update(new Marker(feedUri, feedEntryId), feedEntryId);
    }
}