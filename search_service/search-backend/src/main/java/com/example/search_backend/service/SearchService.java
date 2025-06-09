package com.example.search_backend.service;

import com.example.search_backend.model.UserDocument;
import com.example.search_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SearchService {
    private final UserRepository userRepository;

    public SearchService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(UserDocument user) {
        userRepository.save(user);
    }

    public List<UserDocument> searchByFirstname(String firstname) {
        return userRepository.findByFirstnameContainingIgnoreCase(firstname);
    }
}
