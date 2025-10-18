package com.javabackend.repository;

import com.javabackend.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    // List<User> findByName(String name);
    Page<Task> findByNameContaining(String name, Pageable pageable);
    // List<User> findByOwner(String owner);
}