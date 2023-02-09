package com.foxy.patreon.validator.entity;

import java.time.Instant;
import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.mapper.UpdateBehavior;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbUpdateBehavior;

@DynamoDbBean
public class PatronEntity {

    private String id;
    private String sortKey;
    private String discordId;
   
    private String patronId;

    private String characterName;
    private String name;
    private List<String> tier;
    private String category;
    @DynamoDbAttribute("Category")
    @DynamoDbSecondaryPartitionKey(indexNames = {"categoryIndex"})
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    private CharacterEntity meta;
    @DynamoDbAttribute("CharacterMeta")
    public CharacterEntity getMeta() {
        return meta;
    }
    public void setMeta(CharacterEntity meta) {
        this.meta = meta;
    }
    @DynamoDbAttribute("Tier")
    public List<String> getTier() {
        return tier;
    }
    public void setTier(List<String> tier) {
        this.tier = tier;
    }
    @DynamoDbAttribute("Name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    private String status;
    private Instant creationDate;
    private String image;
    private String userId;
    
    @DynamoDbSecondaryPartitionKey(indexNames = {"discordIdIndex"})
    // usually only the INFO item will have this attribute so this is useless
    @DynamoDbAttribute("DiscordId")
    public String getDiscordId() {
        return discordId;
    }
    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }
    //
    @DynamoDbAttribute("PatronId")
    public String getPatronId() {
        return patronId;
    }
    public void setPatronId(String patronId) {
        this.patronId = patronId;
    }
    @DynamoDbAttribute("CharacterName")
    @DynamoDbSecondaryPartitionKey(indexNames={"characterNameIndex"})
    // get patreon id by character name
    // just use this gsi and ask for the partkey
    public String getCharacterName() {
        return characterName;
    }
    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
    @DynamoDbAttribute("Status")
    @DynamoDbSecondaryPartitionKey(indexNames = {"statusIndex"})
    // get character name by status (only characters will have statuses)
    // just use this gsi and ask for the name
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }


    @DynamoDbAttribute("CreationDate")
    @DynamoDbSecondarySortKey(indexNames = {"characterNameIndex","statusIndex","infoOrCharacterIDIndex"})
    @DynamoDbUpdateBehavior(UpdateBehavior.WRITE_IF_NOT_EXISTS)
    // just handy to be able to sort by date
    public Instant getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Instant date) {
        this.creationDate = date;
    }
    @DynamoDbAttribute("Image")
    // optional, will use this if i want to query the other api to make an album
    // get Image by character name
    // use characterNameIndex and ask for image
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    // This is always going to be the patreon ID
    // PATREON_<PATREONID>
    @DynamoDbPartitionKey
    @DynamoDbAttribute("PartKey")
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    // Usually going to be the character ID
    // CHARACTER_<CHARACTERID>
    @DynamoDbSortKey
    @DynamoDbAttribute("SortKey")
    @DynamoDbSecondaryPartitionKey(indexNames = {"infoOrCharacterIDIndex"})
    public String getSortKey() {
        return sortKey;
    }
    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }
    @Override
    public String toString() {
        return "PatronEntity [id=" + id + ", sortKey=" + sortKey + ", discordId=" + discordId + ", patronId=" + patronId
                + ", characterName=" + characterName + ", name=" + name + ", tier=" + tier + ", category=" + category
                + ", meta=" + meta + ", status=" + status + ", creationDate=" + creationDate + ", image=" + image
                + ", userId=" + userId + "]";
    }
    @DynamoDbAttribute("UserId")
    @DynamoDbSecondaryPartitionKey(indexNames = {"UserId-index"})
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }


    
}
