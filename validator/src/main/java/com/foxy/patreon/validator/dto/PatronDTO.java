package com.foxy.patreon.validator.dto;

import java.time.Instant;

import com.foxy.patreon.validator.entity.PatronEntity;

public class PatronDTO {

    private String id;
    private String sortKey;
    private String discordId;
   
    private String patronId;

    private String characterName;
    private String status;
    private Instant creationDate;
    private String image;

    
    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getSortKey() {
        return sortKey;
    }


    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }


    public String getDiscordId() {
        return discordId;
    }


    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }


    public String getPatronId() {
        return patronId;
    }


    public void setPatronId(String patronId) {
        this.patronId = patronId;
    }


    public String getCharacterName() {
        return characterName;
    }


    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public Instant getCreationDate() {
        return creationDate;
    }


    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }


    public String getImage() {
        return image;
    }


    public void setImage(String image) {
        this.image = image;
    }


    public PatronEntity prepareEntity(PatronDTO patronDTO) {
        PatronEntity patronEntity = new PatronEntity();
        patronEntity.setCharacterName(patronDTO.getCharacterName());
        patronEntity.setCreationDate(patronDTO.getCreationDate());
        patronEntity.setDiscordId(patronDTO.getDiscordId());
        patronEntity.setId(patronDTO.getId());
        patronEntity.setImage(patronDTO.getImage());
        patronEntity.setSortKey(patronDTO.getSortKey());
        patronEntity.setStatus(patronDTO.getStatus());
        patronEntity.setPatronId(patronDTO.getPatronId());
        return patronEntity;
    }
    
}
