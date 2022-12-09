package com.foxy.patreon.validator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foxy.patreon.validator.repository.CharacterRepository;
import com.foxy.patreon.validator.repository.OrderRepository;
import com.foxy.patreon.validator.repository.PatronRepository;

@Service("validatorService")
public class ValidatorServiceImpl implements ValidatorService{
    @Autowired
    CharacterRepository characterRepo;
    @Autowired
    OrderRepository orderRepo;
    @Autowired
    PatronRepository patronRepo;
    
}
