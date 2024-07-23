package edu.escuelaing.arsw.ase.app.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import edu.escuelaing.arsw.ase.app.entity.User;

public interface UserService extends MongoRepository<User, String>{
    public User findByName(String name);


    @Query("{ }")
    List<User> findAllUsersOrderByScoreDesc(Sort sort);
}
