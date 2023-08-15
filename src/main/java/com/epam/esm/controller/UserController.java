package com.epam.esm.controller;

import com.epam.esm.dto.MessageDTO;
import com.epam.esm.dto.UserDTO;
import com.epam.esm.entity.User;
import com.epam.esm.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService userService;

    /**
     * Method for getting all users that exist in DB
     * If there are no any users, empty list will be returned
     *
     * @return all users in JSON format
     */
    @RolesAllowed("ADMIN")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(value = "p", required = false) Integer page) {
        List<UserDTO> userList = userService.getAllUsersWithPagination(page);
        for(UserDTO user : userList) {
            user.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withRel("user"));
        }
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    /**
     * Method for getting user by id
     *
     * @param id is identifier of user
     *
     * @return user in JSON format
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") int id) {
        UserDTO user = userService.getUserDTOById(id);
        user.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
        user.add(linkTo(methodOn(UserController.class).getAllUsers(1)).withRel("users"));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Method for puttin user in DB
     * User contains userName
     *
     * @param user user to create
     */
    @RolesAllowed("ADMIN")
    @PostMapping
    public ResponseEntity<MessageDTO> createUser(@RequestBody User user) {
        int id = userService.createUser(user);
        return ResponseEntity.ok(MessageDTO.builder()
                .status(HttpStatus.OK.value())
                .message("Created user with id " + id)
                .build());
    }
}