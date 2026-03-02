package com.example.ReadBrew.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.ReadBrew.client.BookProviderClient;
import com.example.ReadBrew.dto.BookResponseDTO;
import com.example.ReadBrew.dto.external.ExternalBookItemDTO;
import com.example.ReadBrew.dto.external.ExternalBookResponseDTO;

@Service
public class BookCatalogService {

    @Autowired
    private BookProviderClient bookProviderClient;

    @Value("${google.books.api.key}")
    private String apiKey;

    @Cacheable(value = "bookSearch", key = "#title")
    public List<BookResponseDTO> searchBooks(String title) {
        
      // enhanced search :)
        ExternalBookResponseDTO response = bookProviderClient.searchBooks(title, "full", apiKey);
        
        List<BookResponseDTO> cleanList = new ArrayList<>();

        if (response != null && response.getItems() != null) {
            for (ExternalBookItemDTO item : response.getItems()) {
                if (item.getVolumeInfo() != null) {
                    
                   
                    if (item.getVolumeInfo().getImageLinks() == null) {
                        continue; 
                    }

                    BookResponseDTO book = new BookResponseDTO();
                    book.setId(item.getId());
                    book.setTitle(item.getVolumeInfo().getTitle());
                    book.setAuthors(item.getVolumeInfo().getAuthors());
                    book.setDescription(item.getVolumeInfo().getDescription());
                    book.setPageCount(item.getVolumeInfo().getPageCount());

                   
                    book.setCategories(refineCategories(
                        item.getVolumeInfo().getCategories(), 
                        item.getVolumeInfo().getDescription()
                    ));

             
                    String thumb = item.getVolumeInfo().getImageLinks().getThumbnail();
                    if (thumb != null) {
                        book.setThumbnailUrl(thumb.replace("http://", "https://"));
                    }
                    
                    cleanList.add(book);
                }
            }
        }
        return cleanList;
    }

    //AI-generated mapping ensures the category is always within my tags in the database. 
    // I had to create this refinement because the Google API often returns null 
    // values ​​for important fields.
    
    private List<String> refineCategories(List<String> categories, String description) {
   
    if (categories != null && !categories.isEmpty()) {
        String mainCat = categories.get(0);
        if (!mainCat.equalsIgnoreCase("Fiction") && 
            !mainCat.equalsIgnoreCase("Juvenile Fiction") && 
            !mainCat.equalsIgnoreCase("General")) {
            return categories;
        }
    }

    
    if (description == null || description.isBlank()) return categories;
    String desc = description.toLowerCase();

   
    

    if (desc.contains("horror") || desc.contains("terror") || desc.contains("medo") || 
        desc.contains("assombrado") || desc.contains("sobrenatural") || desc.contains("morte")) {
        return List.of("Horror");
    }


    if (desc.contains("crime") || desc.contains("detetive") || desc.contains("mistério") || 
        desc.contains("investigação") || desc.contains("assassinato") || desc.contains("pista")) {
        return List.of("Mystery");
    }


    if (desc.contains("amor") || desc.contains("apaixonar") || desc.contains("romance") || 
        desc.contains("casamento") || desc.contains("drama") || desc.contains("sentimental")) {
        return List.of("Romance");
    }

  
    if (desc.contains("magia") || desc.contains("dragão") || desc.contains("espada") || 
        desc.contains("reino") || desc.contains("bruxo") || desc.contains("épico") || desc.contains("aventura")) {
        return List.of("Fantasy");
    }


    if (desc.contains("futuro") || desc.contains("espacial") || desc.contains("tecnologia") || 
        desc.contains("robô") || desc.contains("nave") || desc.contains("distopia")) {
        return List.of("Science Fiction");
    }

  
    if (desc.contains("hábito") || desc.contains("sucesso") || desc.contains("carreira") || 
        desc.contains("liderança") || desc.contains("finanças") || desc.contains("estratégia")) {
        return List.of("Business & Economics");
    }

   
    return categories;
}

}