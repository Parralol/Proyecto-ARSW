package edu.escuelaing.arsw.ase.app.service;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.escuelaing.arsw.ase.app.entity.User;

public interface UserService extends MongoRepository<User, String>{
    public User findByName(String name);
}
