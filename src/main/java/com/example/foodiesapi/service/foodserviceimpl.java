package com.example.foodiesapi.service;

import com.example.foodiesapi.entity.foodentity;
import com.example.foodiesapi.io.foodrequest;
import com.example.foodiesapi.io.foodresponse;
import com.example.foodiesapi.repository.foodrepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class foodserviceimpl implements foodservice{
    @Autowired
    private S3Client s3Client;
@Autowired
    private  foodrepository foodrepository;

    @Value("${aws.s3.bucketname}")
    private String bucketname;


    @Override
    public String uploadFile(MultipartFile file) {
        String filenameExtension=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String key=UUID.randomUUID().toString()+"."+filenameExtension;
        try{
            PutObjectRequest putObjectRequest=PutObjectRequest.builder()
                    .bucket(bucketname)
                    .key(key)
                    .acl("public-read")
                    .contentType(file.getContentType())
                    .build();
            PutObjectResponse response=s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        if(response.sdkHttpResponse().isSuccessful()){
            return "https://"+bucketname+".s3.amazonaws.com/"+key;
        }else{
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"File upload failed");
        }
        }catch (IOException e){
throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"An error occured while loading the file");
        }
    }

    @Override
    public foodresponse addFood(foodrequest request, MultipartFile file) {
      foodentity newfoodentity= convertToEntity(request);
      String imageUrl=uploadFile(file);
      newfoodentity.setImageUrl(imageUrl);
      newfoodentity=foodrepository.save(newfoodentity);
      return convertToResponse(newfoodentity);

    }

    @Override
    public List<foodresponse> readFoods() {
        List<foodentity> databaseEntries=foodrepository.findAll();
        return databaseEntries.stream().map(object ->convertToResponse(object)).collect(Collectors.toList());
    }

    @Override
    public foodresponse readFood(String id) {
        foodentity existingfood=foodrepository.findById(id).orElseThrow(()->new RuntimeException("Food not found for the id:"+id));
        return convertToResponse(existingfood);

    }

    @Override
    public boolean deleteFile(String filename) {
        DeleteObjectRequest deleteObjectRequest= DeleteObjectRequest.builder()
                .bucket(bucketname)
                .key(filename)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
        return true;
    }

    @Override
    public void deleteFood(String id) {
        foodresponse foodresponse=readFood(id);
        String imageUrl=foodresponse.getImageUrl();
        String filename=imageUrl.substring(imageUrl.lastIndexOf("/")+1);
        boolean isFileDeleted=deleteFile(filename);
        if(isFileDeleted){
            foodrepository.deleteById(foodresponse.getId());
        }

    }

    private foodentity convertToEntity(foodrequest request){
        return foodentity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .build();
    }
    private foodresponse convertToResponse(foodentity entity){
        return foodresponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .build();
    }
}
