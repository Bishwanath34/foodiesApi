package com.example.foodiesapi.repository;

import com.example.foodiesapi.entity.foodentity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface foodrepository  extends MongoRepository<foodentity,String> {

}
