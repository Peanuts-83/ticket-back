package com.example.ticketback.controller;

import com.example.ticketback.domain.entity.User;
import com.example.ticketback.dto.common.BaseHttpParams;
import com.example.ticketback.dto.common.HttpPostResult;
import com.example.ticketback.dto.user.UserCreateDto;
import com.example.ticketback.dto.user.UserDto;
import com.example.ticketback.dto.user.UserMetaCreateDto;
import com.example.ticketback.dto.user.UserUpdateDto;
import com.example.ticketback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get/{id}")
    public HttpPostResult<UserDto> get(@PathVariable Long id) {
        return HttpPostResult.of(userService.get(id));
    }

    @PostMapping("/getList")
    public HttpPostResult<List<UserDto>> getList(@RequestBody(required = false)BaseHttpParams params) {
        List<UserDto> users = userService.getList(params);
        return HttpPostResult.ofList(users, (long) users.size());
    }

    @GetMapping("/getUpdate/{id}")
    public HttpPostResult<UserDto> getUpdate(@PathVariable Long id) {
        return HttpPostResult.ofMeta(userService.get(id));
    }

    @PostMapping("/update")
    public HttpPostResult<UserDto> update(@RequestBody UserUpdateDto payload) {
        return HttpPostResult.of(userService.update(payload));
    }

    @GetMapping("/metaCreate")
    public HttpPostResult<UserCreateDto> metaCreate() {
        return HttpPostResult.ofMeta(userService.getMetaCreate());
    }

    @PostMapping("/create")
    public HttpPostResult<UserDto> create(@RequestBody UserCreateDto payload) {
        return HttpPostResult.of(userService.create(payload));
    }
}
