package com.foxy.patreon.validator.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.foxy.patreon.validator.dto.PatronDTO;
import com.foxy.patreon.validator.entity.PatronEntity;

import reactor.core.publisher.Mono;
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
    public void save (PatronEntity patronEntity){
        DynamoDbTable<PatronEntity> table = getTable();
        table.putItem(patronEntity);
    }
    private DynamoDbTable<PatronEntity> getTable(){
        return client.table("Patron", TableSchema.fromBean(PatronEntity.class));
    }
    public void addAll(Mono<ResponseEntity<List<PatronEntity>>> response){
        response.block()
        .getBody()
        .forEach(this::save);
    }
}
