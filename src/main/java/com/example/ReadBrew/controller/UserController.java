package com.example.ReadBrew.controller;
import com.example.ReadBrew.dto.UserRequestDTO;
import com.example.ReadBrew.model.User;
import com.example.ReadBrew.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository

    @GetMapping
    public List<User> listAll(){
        return userRepository.findAll();    
    }


    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequestDTO data){
        if (userRepository.findByEmail(data.getEmail()) != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body("Erro: Este e-mail já está cadastrado.");
        }
    }





    
}
