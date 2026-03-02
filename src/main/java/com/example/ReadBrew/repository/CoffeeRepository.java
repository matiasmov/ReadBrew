package com.example.ReadBrew.repository;

import java.util.List;
import java.util.Optional; 

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.ReadBrew.model.Coffee;

import feign.Param;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    
   
    Optional<Coffee> findByNameContainingIgnoreCase(String name);
    @Query(value = """
        SELECT c.* FROM coffees c
        JOIN coffee_tags ct ON c.id = ct.coffee_id
        JOIN category_tags cat_t ON ct.tag_id = cat_t.tag_id
        WHERE LOWER(cat_t.category_name) IN (:categories)
        GROUP BY c.id
        ORDER BY COUNT(ct.tag_id) DESC
        LIMIT 1
    """, nativeQuery = true)
    Optional<Coffee> findBestMatchByCategories(@Param("categories") List<String> categories);
    
}