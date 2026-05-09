package com.example.ticketback.service;

import com.example.ticketback.domain.entity.User;
import com.example.ticketback.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(@NonNull User user) {
        return userRepository.save(user);
    }

}
