package com.foxy.patreon.validator.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.reactive.function.client.WebClient;

import com.foxy.patreon.validator.entity.PatronEntity;
import com.foxy.patreon.validator.service.ValidatorService;
import com.nimbusds.oauth2.sdk.http.HTTPRequest.Method;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ValidatorApi {
@Autowired
ValidatorService validatorService;
    @Autowired
    WebClient restTemplate;
    
    @GetMapping("/validate")
    public ResponseEntity<String> validate(){
        return ResponseEntity.ok("Hi");
    }
    @RequestMapping(value="/update/{campaign_id}",method={RequestMethod.POST,RequestMethod.GET})
    public Mono<ResponseEntity<String>> updateMembers(@PathVariable("campaign_id") String campaignId){
        // System.out.println(validatorService.updateMembers(campaignId).block());
        // return new ResponseEntity<>("Success",HttpStatus.OK);
        validatorService.updateMembers(campaignId);
    return Mono.just(ResponseEntity.ok("Hi"));
    }
    @RequestMapping(value="/oauth2/redirect", method={RequestMethod.POST,RequestMethod.GET})
    public ResponseEntity<String> callback(@RequestParam("code") String code, @RequestParam("state") String state){
        return ResponseEntity.ok("OH SHIT I'M ALMOST IN. i don't actually know anymore");
    }
}
