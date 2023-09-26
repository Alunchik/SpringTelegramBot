package com.Alekperova.telegram.ImageBot.repository;


import com.Alekperova.telegram.ImageBot.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;



@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByChatId(Long id);
    void deleteByChatId(Long id);
}
