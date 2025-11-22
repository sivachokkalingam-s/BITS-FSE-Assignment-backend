package com.fse.equipment_management.repository;

import com.fse.equipment_management.data.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByUsername(String username);
    AppUser findByRegisterNo(String registerNo);
}
