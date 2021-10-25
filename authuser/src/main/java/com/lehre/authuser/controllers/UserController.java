package com.lehre.authuser.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.lehre.authuser.dtos.UserData;
import com.lehre.authuser.models.UserModel;
import com.lehre.authuser.services.UserService;
import com.lehre.authuser.specs.SpecTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUserById(@PathVariable UUID id) {
    Optional<UserModel> userModelOptional = userService.findById(id);
    if (userModelOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(String.format("User with id %s not found!", id));
    }
    userService.delete(userModelOptional.get());
    return ResponseEntity.status(HttpStatus.OK)
        .body(String.format("User with id %s deleted successfully", id));
  }

  @GetMapping
  public ResponseEntity<Page<UserModel>> getAllUsers(
      SpecTemplate.UserSpec spec,
      @PageableDefault(sort = "creationDate", direction = Sort.Direction.ASC) Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.findAll(spec, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable UUID id) {
    Optional<UserModel> userModelOptional = userService.findById(id);
    if (userModelOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(String.format("User with id %s not found!", id));
    }
    return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(
      @PathVariable UUID id,
      @RequestBody
          @Validated(UserData.UserView.GenericPut.class)
          @JsonView(UserData.UserView.GenericPut.class)
          UserData userData) {
    Optional<UserModel> userModelOptional = userService.findById(id);
    if (userModelOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(String.format("User with id %s not found!", id));
    }
    UserModel userModel = userModelOptional.get();
    userModel.setCpf(userData.getCpf());
    userModel.setFullName(userData.getFullName());
    userModel.setPhoneNumber(userData.getPhoneNumber());
    userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
    userService.save(userModel);
    return ResponseEntity.status(HttpStatus.OK).body(userModel);
  }

  @PutMapping("/{id}/password")
  public ResponseEntity<?> updatePassword(
      @PathVariable UUID id,
      @RequestBody
          @Validated(UserData.UserView.PasswordPut.class)
          @JsonView(UserData.UserView.PasswordPut.class)
          UserData userData) {
    Optional<UserModel> userModelOptional = userService.findById(id);
    if (userModelOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(String.format("User with id %s not found!", id));
    }
    UserModel userModel = userModelOptional.get();
    if (!userModel.getPassword().equals(userData.getOldPassword())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Mismatched old password!");
    }
    userModel.setPassword(userData.getPassword());
    userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
    userService.save(userModel);
    return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully!");
  }

  @PutMapping("/{id}/image")
  public ResponseEntity<?> updateImage(
      @PathVariable UUID id,
      @RequestBody
          @Validated(UserData.UserView.ImagePut.class)
          @JsonView(UserData.UserView.ImagePut.class)
          UserData userData) {
    Optional<UserModel> userModelOptional = userService.findById(id);
    if (userModelOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(String.format("User with id %s not found!", id));
    }
    UserModel userModel = userModelOptional.get();
    userModel.setImageUrl(userData.getImageUrl());
    userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
    userService.save(userModel);
    return ResponseEntity.status(HttpStatus.OK).body(userModel);
  }
}
