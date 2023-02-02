package com.foxy.patreon.validator.repository;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collector;

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
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.ssm.SsmClient;

@Repository
public class PatronRepository {
    final static Logger logger = LoggerFactory.getLogger(PatronRepository.class);
    @Autowired
    DynamoDbTable<PatronEntity> table;
    @Autowired
    DynamoDbEnhancedClient client;
    public Mono<Void> save(PatronDTO patronDTO){
        PatronEntity patronEntity= patronDTO.prepareEntity(patronDTO);
        return save(patronEntity);
    }
    public Mono<Void> save (PatronEntity patronEntity){
        return Mono.fromRunnable(()-> table.putItem(patronEntity));
    }
    public  Flux<PatronEntity> addAll(Mono<ResponseEntity<List<PatronEntity>>> response){

        return batchWrite(response.mapNotNull(ResponseEntity::getBody)
        .flatMapIterable(i->i)); // turns it into a Flux of Patron Entities 
         // updateItem takes a PatronEntity and returns a Patron Entity
    }
    public Flux<PatronEntity> batchWrite(Flux<PatronEntity> list){
        return list
        .map(a->{
            a.setCreationDate(Instant.now());
        return a;})
        .window(25) // bulk request max size is 25
        .flatMap(t ->
         // put everything in the batch
            t.collect(()->WriteBatch.builder(PatronEntity.class)
            .mappedTableResource(table),
          (x, y)->{
            x.addPutItem(y);
         })
         // change the builder into the write request
         .map(a->BatchWriteItemEnhancedRequest.builder()
         .addWriteBatch(a.build())
         .build())
        )
        // send off the batch write request
        .map(
            client::batchWriteItem
        )
        // read the result
        .map(
            a->
            a.unprocessedPutItemsForTable(table)
        )
        .flatMapIterable(l->l) //return unprocessed items
        ;
    }
    public  Flux<PatronEntity> addAll(Flux<ResponseEntity<PatronEntity>> response){
        return batchWrite(response
        .mapNotNull(ResponseEntity::getBody));
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
        return Mono.fromCallable(()->table.updateItem(
            UpdateItemEnhancedRequest.builder(PatronEntity.class)
            .item(patronEntity)
            .ignoreNulls(true) // partial update
            .build()
            ));
    }
    public Mono<PatronEntity> deleteCharacter(PatronEntity patronEntity) {
        return null;
    }
}
