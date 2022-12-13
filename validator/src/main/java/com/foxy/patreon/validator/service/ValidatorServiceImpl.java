package com.foxy.patreon.validator.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.foxy.patreon.validator.entity.PatronEntity;
import com.foxy.patreon.validator.repository.PatronRepository;
import net.minidev.json.JSONObject;
import reactor.core.publisher.Mono;

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
        reqJsonObject.put("include",List.of("currently_entitled_tiers","user"));
        Map<String,List<String>> fields = new HashMap<>();
        fields.put("user",List.of("social_connections"));
        fields.put("member",List.of("patron_status"));
        fields.put("tier",List.of("title"));
        reqJsonObject.put("fields", fields);
        var result=rest.post()
        .uri("http://coolwhip_2:65010/campaign/members?campaign_id="+campaignId)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(reqJsonObject)
         .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .toEntityList(PatronEntity.class);
        // System.out.println(result.block());
        patronRepo.addAll(result);
        // return result;
    }
    public Mono<PatronEntity> findByDiscordId(String discordId){
        return null;
    }
    public Mono<PatronEntity> findByPatronId(String patronId){
        return null;
    }
    @Override
    public Mono<PatronEntity> findById(PatronEntity patronEntity) {
        // TODO Auto-generated method stub
        final Pattern matcher = Pattern.compile(".*");
        final Pattern matcher2 = Pattern.compile(".*");
        if(patronEntity.getPatronId() !=null && matcher.matcher(patronEntity.getPatronId()).matches()  ){
            return findByPatronId(patronEntity.getPatronId());
        }
        if(patronEntity.getDiscordId() !=null &&
        matcher2.matcher(patronEntity.getDiscordId()).matches())
        {
            return findByDiscordId(patronEntity.getDiscordId());
        }
    if(patronEntity.getId() !=null){
        return patronRepo.findById(patronEntity.getId());
    }
    return Mono.empty();
}
    
}
