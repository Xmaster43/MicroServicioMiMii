package com.mimi.ReservaService.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CloudEvent<T> {
    private String specversion = "1.0";
    private String id = UUID.randomUUID().toString();
    private String source;
    private String type;
    private OffsetDateTime time = OffsetDateTime.now();
    private String datacontenttype = "application/json";
    private T data;

    public CloudEvent() {}

    public CloudEvent(String source, String type, T data) {
        this.source = source;
        this.type = type;
        this.data = data;
    }

    // getters and setters
    public String getSpecversion() { return specversion; }
    public void setSpecversion(String specversion) { this.specversion = specversion; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public OffsetDateTime getTime() { return time; }
    public void setTime(OffsetDateTime time) { this.time = time; }
    public String getDatacontenttype() { return datacontenttype; }
    public void setDatacontenttype(String datacontenttype) { this.datacontenttype = datacontenttype; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}