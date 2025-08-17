package com.example.foodiesapi.service;

import com.example.foodiesapi.io.foodrequest;
import com.example.foodiesapi.io.foodresponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface foodservice {
    String uploadFile(MultipartFile file);
     foodresponse addFood(foodrequest request, MultipartFile file);
     List<foodresponse> readFoods();
     foodresponse readFood(String id);
     boolean deleteFile(String filename);
     void deleteFood(String id);
}
