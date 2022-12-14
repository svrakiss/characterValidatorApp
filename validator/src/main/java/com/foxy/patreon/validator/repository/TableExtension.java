package com.foxy.patreon.validator.repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.IndexMetadata;
import software.amazon.awssdk.enhanced.dynamodb.TableMetadata;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedLocalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.Stream;

public class TableExtension {
    public static CreateTableEnhancedRequest createTableEnhancedRequestWithIndices(TableMetadata tableMetadata,
            Function<IndexMetadata, Projection> createProjection) {

        Stream<EnhancedGlobalSecondaryIndex> globalIndices = tableMetadata.indices()
                .stream()
                .filter(it -> it.partitionKey().get().name() != tableMetadata.primaryPartitionKey())
                .map(
                        index -> {
                            return EnhancedGlobalSecondaryIndex.builder()
                                    .indexName(index.name())
                                    .projection(createProjection.apply(index))
                                    .build();
                        });

        Stream<EnhancedLocalSecondaryIndex> localIndices = tableMetadata.indices()
                .stream()
                .filter(it -> it.partitionKey().get().name() == tableMetadata.primaryPartitionKey()
                        && it.name() != TableMetadata.primaryIndexName())
                .map(
                        index -> {
                            return EnhancedLocalSecondaryIndex.builder()
                                    .indexName(index.name())
                                    .projection(createProjection.apply(index))
                                    .build();
                        });

        var result = CreateTableEnhancedRequest.builder();
        var globalIndicesL = globalIndices.toList();
        if (!globalIndicesL.isEmpty()) {
            result = result.globalSecondaryIndices(globalIndicesL);
        }
        var localIndicesL = localIndices.toList();
        if (!localIndicesL.isEmpty()) {
            result = result.localSecondaryIndices(localIndicesL);
        }
        return result.build();
    }

    private static final Function<IndexMetadata, Projection> defaultProjectionBuilder = i -> Projection.builder()
            .projectionType(ProjectionType.ALL).build();

    public static <T> void createTableWithIndices(DynamoDbTable<T> table) {
        var request = createTableEnhancedRequestWithIndices(table.tableSchema().tableMetadata(),
                defaultProjectionBuilder);
        table.createTable(request);
    }

    public static <T> void createTableWithIndices(DynamoDbTable<T> table,
            Function<IndexMetadata, Projection> createProjection) {
        var request = createTableEnhancedRequestWithIndices(table.tableSchema().tableMetadata(), createProjection);
        table.createTable(request);
    }

    public static <T> CompletableFuture<Void> createTableWithIndices(DynamoDbAsyncTable<T> table) {
        var request = createTableEnhancedRequestWithIndices(table.tableSchema().tableMetadata(),
                defaultProjectionBuilder);
        return table.createTable(request);
    }

    public static <T> CompletableFuture<Void> createTableWithIndices(DynamoDbAsyncTable<T> table,
            Function<IndexMetadata, Projection> createProjection) {
        var request = createTableEnhancedRequestWithIndices(table.tableSchema().tableMetadata(), createProjection);
        return table.createTable(request);
    }

}