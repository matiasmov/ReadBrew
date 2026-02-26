package com.example.ReadBrew.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ReadBrew.model.Coffee;
import com.example.ReadBrew.repository.CoffeeRepository;

@RestController
@RequestMapping("/api/v1/coffees")
public class CoffeeController {

    @Autowired
    private CoffeeRepository coffeeRepository;

 
    @PostMapping
    public ResponseEntity<Coffee> createCoffee(@RequestBody Coffee coffee) {
        Coffee savedCoffee = coffeeRepository.save(coffee);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCoffee);
    }

   
    @GetMapping
    public ResponseEntity<List<Coffee>> getAllCoffees() {
        return ResponseEntity.ok(coffeeRepository.findAll());
    }
}