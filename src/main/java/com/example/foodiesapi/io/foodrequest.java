package com.example.foodiesapi.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class foodrequest {
    private String name;
    private String description;
    private double price;
    private String category;
}
