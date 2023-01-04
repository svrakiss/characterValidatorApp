package com.foxy.patreon.validator.service;

import com.foxy.patreon.validator.entity.PatronEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ValidatorService{

    void updateMembers(String campaignId);
    Flux<PatronEntity> updateMembers(String campaignId, Integer pageSize);
    Mono<PatronEntity> findById(PatronEntity patronEntity);
    Mono<PatronEntity> deletePatron(PatronEntity patronEntity);
    Mono<PatronEntity> deleteCharacter(PatronEntity patronEntity);
    Mono<PatronEntity> addCharacter(PatronEntity patronEntity);

}