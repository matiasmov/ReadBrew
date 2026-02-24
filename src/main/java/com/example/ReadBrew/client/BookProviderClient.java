package com.example.ReadBrew.client; // ou .client

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ReadBrew.dto.external.ExternalBookResponseDTO;


@FeignClient(name = "bookProvider", url = "${external.book.api.url}") 
public interface BookProviderClient {
    
    
    @GetMapping("/volumes")
    ExternalBookResponseDTO searchBooks(@RequestParam("q") String query, @RequestParam("key") String apiKey);
}