package com.ust.auth_service.repo;

import com.ust.auth_service.model.Account;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepo extends MongoRepository<Account, ObjectId> {
    Optional<Account> findByEmail(String email);
}
