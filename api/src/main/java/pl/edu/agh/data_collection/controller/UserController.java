package pl.edu.agh.data_collection.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.data_collection.config.ContextPath;
import pl.edu.agh.data_collection.config.UserRole;
import pl.edu.agh.data_collection.dto.UserDto;
import pl.edu.agh.data_collection.exception.BadCredentialsException;
import pl.edu.agh.data_collection.persistence.entity.UserEntity;
import pl.edu.agh.data_collection.persistence.repository.UserRepository;

import javax.validation.Valid;
import java.util.Optional;

import static pl.edu.agh.data_collection.exception.BadCredentialsException.ExceptionMessage.*;

@RestController
@RequestMapping(ContextPath.USER_MAIN_PATH)
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(ContextPath.USER_CREATE_PATH)
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto, Errors errors) throws BadCredentialsException {
        if(errors.hasErrors())
            throw new BadCredentialsException(BAD_LOGIN_OR_PASSWORD);

        Optional<UserEntity> foundUserOptional = userRepository.findByLogin(userDto.getLogin());

        if(foundUserOptional.isPresent())
            throw new BadCredentialsException(USER_ALREADY_EXISTS);

        UserEntity userToSave = new UserEntity();
        userToSave.setLogin(userDto.getLogin());
        userToSave.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userToSave.setAuthority(UserRole.USER);

        userRepository.save(userToSave);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping(ContextPath.USER_CHECK_CREDENTIALS_PATH)
    public ResponseEntity<Object> checkUserCredentials(@Valid @RequestBody UserDto userDto, Errors errors) throws BadCredentialsException {
        if(errors.hasErrors())
            throw new BadCredentialsException(BAD_LOGIN_OR_PASSWORD);

        Optional<UserEntity> foundUserOptional = userRepository.findByLogin(userDto.getLogin());

        if(!foundUserOptional.isPresent())
            throw new BadCredentialsException(BAD_LOGIN_OR_PASSWORD);

        boolean isValidPassword = foundUserOptional.map(userEntity -> passwordEncoder.matches(userDto.getPassword(), userEntity.getPassword())).orElse(false);

        if(!isValidPassword)
            throw new BadCredentialsException(BAD_LOGIN_OR_PASSWORD);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
