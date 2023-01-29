package com.foxy.patreon.validator.api;

import org.apache.commons.logging.Log;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
// import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.reactive.function.client.WebClient;

import com.foxy.patreon.validator.entity.PatronEntity;
import com.foxy.patreon.validator.service.ValidatorService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ValidatorApi {
@Autowired
ValidatorService validatorService;
    @Autowired
    WebClient restTemplate;
    org.slf4j.Logger logger= LoggerFactory.getLogger(ValidatorApi.class);
    @GetMapping("/validate")
    public ResponseEntity<String> validate(){
        return ResponseEntity.ok("Hi");
    }
    @RequestMapping(value="/update/{campaign_id}",method={RequestMethod.PUT,RequestMethod.GET})
    public Flux<PatronEntity> updateMembers(@PathVariable("campaign_id") String campaignId,@RequestParam(defaultValue = "100",name = "pageSize") Integer pageSize ){

        return validatorService.updateMembers(campaignId,pageSize);
    }

    @RequestMapping(value="/{campaign_id}",method={RequestMethod.PUT,RequestMethod.GET})
    public Flux<PatronEntity> updateMember(@PathVariable("campaign_id") String campaignId,@RequestParam(defaultValue = "100",name = "pageSize") Integer pageSize, @RequestBody Flux<PatronEntity> members ){

        return validatorService.updateMember(campaignId,pageSize,members);

    }

    @DeleteMapping("/member")
    public Mono<PatronEntity> deletePatron(@RequestBody PatronEntity patronEntity){
        return validatorService.deletePatron(patronEntity);
    }
    @DeleteMapping("/character")
    public Mono<PatronEntity> deleteCharacter(@RequestBody PatronEntity patronEntity){
        return validatorService.deleteCharacter(patronEntity);
    }
    @PostMapping("/character")
    public Mono<PatronEntity> addCharacter(@RequestBody PatronEntity patronEntity){
        logger.info(patronEntity.toString());
        return validatorService.addCharacter(patronEntity);
    }
    @RequestMapping(value="/oauth2/redirect", method={RequestMethod.POST,RequestMethod.GET})
    public ResponseEntity<String> callback(@RequestParam("code") String code, @RequestParam("state") String state){
    
        return ResponseEntity.ok("OH SHIT I'M ALMOST IN. i don't actually know anymore");
    }
    @RequestMapping(value="/member",method={RequestMethod.POST,RequestMethod.GET})
    public Mono<PatronEntity> checkById(@RequestBody PatronEntity patronEntity)
    {
    return (validatorService.findById(patronEntity));
}
@GetMapping("/")
public ResponseEntity<String> healthCheck(){
return ResponseEntity.ok("Leave me alone");
}
}
