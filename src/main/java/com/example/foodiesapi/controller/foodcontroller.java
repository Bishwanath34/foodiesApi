package com.example.foodiesapi.controller;

import com.example.foodiesapi.io.foodrequest;
import com.example.foodiesapi.io.foodresponse;
import com.example.foodiesapi.service.foodservice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@CrossOrigin("*")
@RestController
@RequestMapping("/api/foods")
@AllArgsConstructor
public class foodcontroller {
 private final foodservice foodservice;
    @PostMapping
    public foodresponse addFood(@RequestPart("food") String foodString, @RequestPart("file")MultipartFile file){
        ObjectMapper objectMapper=new ObjectMapper();
        foodrequest request=null;
        try{
            request=objectMapper.readValue(foodString,foodrequest.class);
        }catch (JsonProcessingException ex){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid JSON format");
        }
        foodresponse response=foodservice.addFood(request,file);
        return response;
    }
    @GetMapping
    public List<foodresponse> readFoods(){
        return foodservice.readFoods();
    }
    @GetMapping("/{id}")
    public foodresponse readFood(@PathVariable String id){
        return foodservice.readFood(id);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFood(@PathVariable String id){
        foodservice.deleteFood(id);
    }
}
