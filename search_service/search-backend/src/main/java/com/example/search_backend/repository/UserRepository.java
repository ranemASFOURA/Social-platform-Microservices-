package com.example.search_backend.repository;

import com.example.search_backend.model.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface UserRepository extends ElasticsearchRepository<UserDocument, String> {
    List<UserDocument> findByFirstnameContainingIgnoreCase(String firstname);
}
