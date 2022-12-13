package com.foxy.patreon.validator.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.foxy.patreon.validator.entity.PatronEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ValidatorService{

    void updateMembers(String campaignId);

}