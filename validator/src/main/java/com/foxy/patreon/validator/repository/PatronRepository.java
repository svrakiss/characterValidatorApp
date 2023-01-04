package com.foxy.patreon.validator.repository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.foxy.patreon.validator.dto.PatronDTO;
import com.foxy.patreon.validator.entity.PatronEntity;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Repository
public class PatronRepository {
    final static Logger logger = LoggerFactory.getLogger(PatronRepository.class);
    DynamoDbTable<PatronEntity> table;
    PatronRepository(DynamoDbEnhancedClient client){
        this.table= getTable(client);
    }
    public Mono<Void> save(PatronDTO patronDTO){
        PatronEntity patronEntity= patronDTO.prepareEntity(patronDTO);
        return save(patronEntity);
    }
    public Mono<Void> save (PatronEntity patronEntity){
        return Mono.fromRunnable(()-> table.putItem(patronEntity));
    }
    private DynamoDbTable<PatronEntity> getTable(DynamoDbEnhancedClient client){
        return client.table("PatronTesty", TableSchema.fromBean(PatronEntity.class));
    }
    public  Flux<PatronEntity> addAll(Mono<ResponseEntity<List<PatronEntity>>> response){
        
        return response.mapNotNull(ResponseEntity::getBody)
        .flatMapIterable(i->i) // turns it into a Flux of Patron Entities 
        .map(table::updateItem); // updateItem takes a PatronEntity and returns a Patron Entity
    }

    public Mono<PatronEntity> findById(String id) {
        Key key = Key.builder().partitionValue(id).sortValue("INFO").build();
        try{
            return Mono.just(table.getItem(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Mono.empty();
    }
    public Mono<PatronEntity> findFirstInfoByDiscordId(String discordId){
        // Only the repository should touch the indices
        DynamoDbIndex<PatronEntity> index =table.index("discordIdIndex");
        Key key = Key.builder().partitionValue(discordId).build();
        QueryConditional queryConditional = QueryConditional.keyEqualTo(key);
        var result =index.query(r->r.queryConditional(queryConditional)
        .filterExpression(Expression.builder()
        .expression("SortKey = :value")
        .expressionValues(Map.of(":value",AttributeValue.fromS("INFO")))
        .build() )).iterator().next().items().stream().min( Comparator.comparing(PatronEntity::getCreationDate));
        // same discordid but has sortkey INFO

        // grab the first one
        return Mono.justOrEmpty(result);
    }
    public Mono<PatronEntity> delete(PatronEntity patronEntity,String indexName) {
        return Mono.just(table.deleteItem(table.index(indexName).keyFrom(patronEntity)));
    }
    public Mono<PatronEntity> addCharacter(PatronEntity patronEntity) {
        return null;
    }
    public Mono<PatronEntity> deleteCharacter(PatronEntity patronEntity) {
        return null;
    }
}
