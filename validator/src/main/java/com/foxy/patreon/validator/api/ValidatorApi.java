package com.foxy.patreon.validator.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.foxy.patreon.validator.service.ValidatorService;

@RestController
public class ValidatorApi {
@Autowired
ValidatorService validatorService;
    @Autowired
    RestTemplate restTemplate;
    @GetMapping("/validate")
    public ResponseEntity<String> validate(){
        return ResponseEntity.ok("Hi");
    }
    @PostMapping("/update/{campaign_id}")
    public ResponseEntity<String> updateMembers(@PathVariable("campaign_id") String campaignId){
        validatorService.updateMembers(campaignId);
        return new ResponseEntity<>("Success",HttpStatus.OK);
    }
}
