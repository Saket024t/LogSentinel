package com.logsentinel.ingestion;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "logs")
public class LogDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String message;

    @Field(type = FieldType.Keyword)
    private String serviceName;

    @Field(type = FieldType.Keyword)
    private String teamOwner;

    @Field(type = FieldType.Keyword)
    private String criticality;

    @Field(type = FieldType.Keyword)
    private String level;

    @Field(type = FieldType.Date)
    private String timestamp;

    private String rawJson;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getTeamOwner() { return teamOwner; }
    public void setTeamOwner(String teamOwner) { this.teamOwner = teamOwner; }
    public String getCriticality() { return criticality; }
    public void setCriticality(String criticality) { this.criticality = criticality; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getRawJson() { return rawJson; }
    public void setRawJson(String rawJson) { this.rawJson = rawJson; }
}
