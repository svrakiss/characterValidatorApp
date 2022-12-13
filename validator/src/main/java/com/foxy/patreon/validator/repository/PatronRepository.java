package com.foxy.patreon.validator.repository;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.foxy.patreon.validator.dto.PatronDTO;
import com.foxy.patreon.validator.entity.PatronEntity;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class PatronRepository {
    final static Logger logger = LoggerFactory.getLogger(PatronRepository.class);
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
        ResponseEntity<List<PatronEntity>> answer = response.block();
        if(answer.hasBody()){
        DynamoDbTable<PatronEntity> table = getTable();
            
        answer.getBody()
        .forEach(e->table.updateItem(e));
        }
    }
    public Mono<PatronEntity> findById(String id) {
        DynamoDbTable<PatronEntity> table =getTable();
        Key key = Key.builder().partitionValue(id).sortValue("INFO").build();
        try{
            return Mono.just(table.getItem(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Mono.empty();
    }
}
