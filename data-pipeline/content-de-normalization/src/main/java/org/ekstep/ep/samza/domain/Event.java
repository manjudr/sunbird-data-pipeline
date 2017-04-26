package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.search.domain.Content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Event {
    static Logger LOGGER = new Logger(Event.class);
    private Map<String, Object> contentTaxonomy;
    public Map<String, Object> map;

    public Event(Map<String, Object> map, Map<String, Object> contentTaxonomy) {
        this.map = map;
        this.contentTaxonomy = contentTaxonomy;
    }

    public Map<String, Object> getMap(){
        return this.map;
    }

    public String getContentId(){
        for (String event : contentTaxonomy.keySet()) {
            if(getEid().startsWith(event.toUpperCase())){
                ArrayList<String> fields = getRemovableFields(event);
                return getContentId(map, fields);
            }
        }
        return null;
    }

    private ArrayList<String> getRemovableFields(String event) {
        ArrayList<String> fields = new ArrayList<String>();
        fields.addAll((Collection<? extends String>) contentTaxonomy.get(event));
        return fields;
    }

    //TODO: Make this method more readable when writing ItemDenormalizationJob
    private String getContentId(Map<String, Object> map, ArrayList<String> fields) {
        String key = fields.remove(0);
        if(key != null && map.containsKey(key)){
            Object value = map.get(key);
            if(value instanceof String){
                return (String) value;
            }
            return getContentId((Map<String, Object>) map.get(key), fields);
        }
        return null;
    }

    public String id() {
        return getMap() != null && getMap().containsKey("metadata") &&
                (((Map<String, Object>) getMap().get("metadata")).containsKey("checksum"))
                ? (String) ((Map<String, Object>) getMap().get("metadata")).get("checksum")
                : null;
    }

    public void updateContent(Content content) {
        HashMap<String, Object> contentData = new HashMap<String, Object>();
        contentData.put("name", content.name());
        contentData.put("identifier", content.identifier());
        contentData.put("pkgVersion", content.pkgVersion());
        contentData.put("description", content.description());
        contentData.put("mediaType", content.mediaType());
        contentData.put("contentType", content.contentType());
        contentData.put("owner", content.owner());
        contentData.put("lastUpdatedOn", content.lastUpdatedOn());
        contentData.put("duration", content.duration());
        contentData.put("gradeLevel", content.gradeLevel());
        contentData.put("author", content.author());
        contentData.put("code", content.code());
        contentData.put("curriculum", content.curriculum());
        contentData.put("developer", content.developer());
        contentData.put("domain", content.domain());
        contentData.put("downloadUrl", content.downloadUrl());
        contentData.put("edition", content.edition());
        contentData.put("genre", content.genre());
        contentData.put("medium", content.medium());
        contentData.put("portalOwner", content.portalOwner());
        contentData.put("publication", content.publication());
        contentData.put("publisher", content.publisher());
        contentData.put("source", content.source());
        contentData.put("status", content.status());
        contentData.put("subject", content.subject());
        contentData.put("templateType", content.templateType());
        contentData.put("theme", content.theme());

        contentData.put("downloads", content.downloads());
        contentData.put("popularity", content.popularity());
        contentData.put("rating", content.rating());
        contentData.put("size", content.size());
        contentData.put("me_totalDevices", content.me_totalDevices());
        contentData.put("me_totalDownloads", content.me_totalDownloads());
        contentData.put("me_totalInteractions", content.me_totalInteractions());
        contentData.put("me_totalRatings", content.me_totalRatings());
        contentData.put("me_totalSessionsCount", content.me_totalSessionsCount());
        contentData.put("me_totalSideloads", content.me_totalSideloads());
        contentData.put("me_totalTimespent", content.me_totalTimespent());
        contentData.put("me_totalUsage", content.me_totalUsage());

        contentData.put("language", content.language());
        contentData.put("ageGroup", content.ageGroup());
        contentData.put("keywords", content.keywords());
        contentData.put("concepts", content.concepts());
        contentData.put("collaborators", content.collaborators());
        contentData.put("collections", content.collections());
        contentData.put("methods", content.methods());
        contentData.put("words", content.words());

        map.put("contentdata",contentData);

        updateMetadata(content);
    }

    private void updateMetadata(Content content) {
        Map<String, Object> metadata = (Map<String, Object>) map.get("metadata");
        if (metadata != null) {
            metadata.put("cachehit",content.getCacheHit());
            return;
        }
        metadata = new HashMap<String, Object>();
        metadata.put("cachehit",content.getCacheHit());
        map.put("metadata", metadata);

        LOGGER.info(id(), "METADATA CACHEHIT - ADDED " + metadata);
    }

    public String getEid() {
        return map != null && map.containsKey("eid") ? (String) map.get("eid") : null;
    }
}
