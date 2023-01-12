package com.foxy.patreon.validator.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bouncycastle.crypto.EphemeralKeyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.foxy.patreon.validator.entity.PatronEntity;
import com.foxy.patreon.validator.repository.PatronRepository;


import net.minidev.json.JSONObject;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service("validatorService")
public class ValidatorServiceImpl implements ValidatorService{

    @Autowired
    PatronRepository patronRepo;
    @Autowired
    WebClient rest;
   
    @Value("${lambdaEndpoint}") String endPoint;
    @Override
    public void updateMembers(String campaignId){
        updateMembers(campaignId, 100);
    }
        class GateWayTimeoutException extends Exception{

        }
    @Override
    public Flux<PatronEntity> updateMembers(String campaignId, Integer pageSize) {
        
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
        var result=rest.post( )
        .uri(endPoint+"campaign/members?campaign_id="+campaignId+"&page_size="+pageSize)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(reqJsonObject)
         .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(
            e->e.value()==504, 
            response-> Mono.error(new GateWayTimeoutException()))
        .toEntityList(PatronEntity.class)
        .retryWhen(Retry.backoff(4, Duration.ofSeconds(5))
        .filter(t->t instanceof GateWayTimeoutException));
        return patronRepo.addAll(result);
   
    }
    public Mono<PatronEntity> findByDiscordId(String discordId){
        return patronRepo.findFirstInfoByDiscordId(discordId);
    }
    public Mono<PatronEntity> findByPatronId(String patronId){
        return patronRepo.findById("PATREON_"+patronId);
    }
    @Override
    public Mono<PatronEntity> findById(PatronEntity patronEntity) {
        // TODO Determine schema for patron id and discord id
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
    @Override
    public Mono<PatronEntity> deletePatron(PatronEntity patronEntity) {
        // Easiest to use the primary index
        
        return patronRepo.delete(patronEntity,pickIndex(patronEntity));
    }
    private String pickIndex(PatronEntity patronEntity) {
        return null;
    }
    @Override
    public Mono<PatronEntity> deleteCharacter(PatronEntity patronEntity) {
        return patronRepo.deleteCharacter(patronEntity);
    }
    @Override
    public Mono<PatronEntity> addCharacter(PatronEntity patronEntity) {
        if(patronEntity.getSortKey() == null){
            return Mono.error(()->new Exception("There's nothing to identify this entry!"));
        }
        if(patronEntity.getId() == null){
            if( patronEntity.getPatronId() == null){
                if(patronEntity.getDiscordId() == null){
                    return Mono.error(()->new Exception("There's nothing to identify this entry!"));
                }
                    return findByDiscordId(patronEntity.getDiscordId())
                    .map(p-> {
                        patronEntity.setId(p.getId());
                        return patronEntity;
                    })
                    .flatMap(
                        p->patronRepo.addCharacter(p)
                    );
            }
            return findByPatronId(patronEntity.getPatronId()) // if this doesn't throw an error, everything is fine
            .map(p->{
                patronEntity.setId(p.getId()); // pretty much only allow people with a valid patron id to submit
                return patronEntity;
            })
            .flatMap(
                p->patronRepo.addCharacter(p)
            );
        }
        return patronRepo.addCharacter(patronEntity);
    }
    
}
