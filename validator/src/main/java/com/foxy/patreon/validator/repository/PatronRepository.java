package com.foxy.patreon.validator.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.foxy.patreon.validator.dto.PatronDTO;
import com.foxy.patreon.validator.entity.PatronEntity;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class PatronRepository {
    @Autowired
    DynamoDbEnhancedClient client;
    public void save(PatronDTO patronDTO){
        PatronEntity patronEntity= patronDTO.prepareEntity(patronDTO);
        DynamoDbTable<PatronEntity> table = getTable();
        table.putItem(patronEntity);
    }
    private DynamoDbTable<PatronEntity> getTable(){
        return client.table("Patron", TableSchema.fromBean(PatronEntity.class));
    }
}
