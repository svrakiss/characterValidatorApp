package com.foxy.patreon.validator.service;

import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;

public interface ValidatorService{

    Mono<ResponseEntity<String>> updateMembers(String campaignId);

}