package com.example.ticketback.service;

import com.example.ticketback.domain.entity.User;
import com.example.ticketback.domain.enums.Role;
import com.example.ticketback.dto.common.BaseHttpParams;
import com.example.ticketback.dto.common.HttpPostResult;
import com.example.ticketback.dto.user.UserCreateDto;
import com.example.ticketback.dto.user.UserDto;
import com.example.ticketback.dto.user.UserMetaCreateDto;
import com.example.ticketback.dto.user.UserUpdateDto;
import com.example.ticketback.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.Arrays;
import java.util.List;

/**
 * Centralisation de la logique
 * read/create/update et mapping Entity <-> DTO
 */
@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto get(Long id) {
        User user = findUserOrThrow(id);
        return toDto(user);
    }

    public List<UserDto> getList(BaseHttpParams params) {
        Pageable pageable = (Pageable) PageRequest.of(
                params.resolvedparamList().pageNum(),
                params.resolvedparamList().nb()
        );
        List<User> userList = userRepository.findList((org.springframework.data.domain.Pageable) pageable);
        return userList
                .stream()
                .map(this::toDto)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public UserUpdateDto getUpdate(Long id) {
        User user = findUserOrThrow(id);
        return toUpdateDto(user);
    }

    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#dto.id()")
    public UserDto update(UserUpdateDto dto) {
        if (dto == null || dto.id() == null) {
            throw new IllegalArgumentException("User id is required for update");
        }
        User user = findUserOrThrow(dto.id());
        user.setUsername(dto.userName());
        user.setEmail(dto.email());
        user.setRole(dto.role());
        User updatedUser = userRepository.save(user);
        return toDto(updatedUser);
    }

    public UserCreateDto getMetaCreate() {
        return new UserCreateDto(null, null, null, null);
    }

    // TODO: hasher le pwd avec BCrypt
    public UserDto create(UserCreateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("User paload is required for create");
        }
        // check de l'existant
        if (userRepository.existsByUsername(dto.userName())) {
            throw new IllegalArgumentException("Username is already in use");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = new User(
                dto.userName(),
                dto.email(),
                dto.password(),
                dto.role() != null ? dto.role() : Role.ROLE_USER
        );
        User savedUser = userRepository.save(user);
        return toDto(savedUser);
    }

    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public Long delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User id is required for delete");
        }
        // check de l'existant
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id " + id);
        }
        userRepository.deleteById(id);
        return id;
    }


    /**
     * Recherche un user ou lève une exception simple.
     *
     * Pour l'instant IllegalArgumentException suffit.
     * Plus tard, on pourra créer une NotFoundException
     * et un GlobalExceptionHandler.
     */
    private User findUserOrThrow(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User not found with id: null");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    private UserUpdateDto toUpdateDto(User user) {
        return new UserUpdateDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}
