package com.foxy.patreon.validator.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class CharacterEntity {
    
    private String source;
    private String artist;
    @DynamoDbAttribute("Artist")
    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    private String comments;
    @DynamoDbAttribute("Comments")
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    @DynamoDbAttribute("Source")
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    @Override
    public String toString() {
        return "CharacterEntity [source=" + source + ", artist=" + artist + ", comments=" + comments + "]";
    }
    
}
