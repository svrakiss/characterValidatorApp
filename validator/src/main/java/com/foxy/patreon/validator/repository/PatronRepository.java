package com.foxy.patreon.validator.repository;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.foxy.patreon.validator.entity.PatronEntity;
@EnableScan
public interface PatronRepository extends CrudRepository<PatronEntity,String>{
    
}
