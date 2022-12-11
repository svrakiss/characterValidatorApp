package com.foxy.patreon.validator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.foxy.patreon.validator.repository.PatronRepository;

@Service("validatorService")
public class ValidatorServiceImpl implements ValidatorService{

    @Autowired
    PatronRepository patronRepo;

    @Override
    public void updateMembers(String campaignId) {
        // TODO Auto-generated method stub
        
    }
    
}
