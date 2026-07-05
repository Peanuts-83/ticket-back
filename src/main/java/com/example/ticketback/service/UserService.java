package com.example.ticketback.service;

import com.example.ticketback.domain.entity.User;
import com.example.ticketback.domain.enums.UserRole;
import com.example.ticketback.dto.common.BaseHttpParams;
import com.example.ticketback.dto.user.UserFormDto;
import com.example.ticketback.dto.user.UserDto;
import com.example.ticketback.dto.user.UserListDto;
import com.example.ticketback.repository.UserRepository;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Centralisation de la logique
 * read/create/update et mapping Entity <-> DTO
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public UserDto get(Long id) {
        User user = findUserOrThrow(id);
        return toDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserListDto> getList(@Nullable BaseHttpParams params) {
        Pageable pageable = params != null && params.paramList() != null ?
                PageRequest.of(
                params.resolvedparamList().pageNum(),
                params.resolvedparamList().nb()
        ) : Pageable.unpaged();
        List<User> userList = userRepository.findList( pageable);
        return userList
                .stream()
                .map(this::toDtoList)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public UserFormDto getUpdate(Long id) {
        User user = findUserOrThrow(id);
        return toUpdateDto(user);
    }

    // TODO: prévoir un formulaire spécifique pour renouveler le pwd
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#dto.id()")
    public UserDto update(UserFormDto dto) {
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

    @PreAuthorize("permitAll")
    public UserFormDto getMetaCreate() {
        return new UserFormDto(null, null, null, null, null, null);
    }

    @PreAuthorize("permitAll")
    public UserDto create(UserFormDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("User payload is required for create");
        }
        if (dto.password() == null || dto.password().isEmpty()) {
            throw new IllegalArgumentException("Password is required for create");
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
                dto.role() != null ? dto.role() : UserRole.USER,
                null,
                null,
                dto.avatar()
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
     * <p>
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
                user.getRole(),
                user.getDt_created(),
                user.getStatus(),
                user.getAvatar()
        );
    }

    private UserFormDto toUpdateDto(User user) {
        return new UserFormDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                null,
                user.getAvatar()
        );
    }

    private UserListDto toDtoList(User user) {
        return new UserListDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getDt_created(),
                user.getStatus()
        );
    }
}
