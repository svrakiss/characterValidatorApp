package com.foxy.patreon.validator.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.foxy.patreon.validator.repository.PatronRepository;

import net.minidev.json.JSONObject;

@Service("validatorService")
public class ValidatorServiceImpl implements ValidatorService{

    @Autowired
    PatronRepository patronRepo;
    @Autowired
    WebClient rest;
    @Override
    public void updateMembers(String campaignId) {
        
        JSONObject reqJsonObject = new JSONObject();
        // need to explicitly state includes fields
        // includes entries have to be plural.
        // fields entries have to be singular
        reqJsonObject.put("include",List.of("users"));
        reqJsonObject.put("fields[user]",List.of("social_connections"));
        reqJsonObject.put("fields[member]",List.of("patron_status"));
        reqJsonObject.put("fields[tier]",List.of("title"));
        var result=rest.get()
        .uri("http://coolwhip_2:65010/campaign/members?campaign_id="+campaignId, reqJsonObject)
        .retrieve()
        .bodyToMono(String.class);
        System.out.println(result);
    }
    
}
