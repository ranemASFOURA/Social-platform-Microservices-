package com.example.user_backend.repository;

import com.example.user_backend.dto.UserEventDTO;
import com.example.user_backend.model.User;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    List<User> findByFirstnameContainingIgnoreCase(String firstname);

    @Aggregation("{ $sample: { size: ?0 } }")
    List<UserEventDTO> findRandomUsers(int size);

}
