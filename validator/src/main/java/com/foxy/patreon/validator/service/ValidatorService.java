package com.foxy.patreon.validator.service;

import com.foxy.patreon.validator.entity.PatronEntity;

import reactor.core.publisher.Mono;

public interface ValidatorService{

    void updateMembers(String campaignId);

    Mono<PatronEntity> findById(PatronEntity patronEntity);

}