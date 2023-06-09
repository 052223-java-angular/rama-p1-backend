package com.revature.cookbook.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.revature.cookbook.dtos.requests.NewLoginRequest;
import com.revature.cookbook.dtos.requests.NewUserRequest;
import com.revature.cookbook.dtos.responses.Principal;
import com.revature.cookbook.entities.Role;
import com.revature.cookbook.entities.User;
import com.revature.cookbook.repositories.UserRepository;
import com.revature.cookbook.utils.custom_exceptions.UserNotFoundException;

import lombok.AllArgsConstructor;

import com.revature.cookbook.services.JwtTokenService;

/**
 * The UserService class provides operations related to user management.
 */
@Service
@AllArgsConstructor
public class UserService {
    private final RoleService roleService;
    private final UserRepository userRepo;
    private final JwtTokenService jwtTokenService;
   

    /**
     * Registers a new user based on the provided information.
     *
     * @param req the NewUserRequest object containing user registration details
     * @return the newly registered User object
     */
    public User registerUser(NewUserRequest req) {
        // find role USER
        Role foundRole = roleService.findByName("USER");

        // hash password
        String hashed = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());

        // create new user
        User newUser = new User(req.getUsername(), hashed, foundRole);

        // save and return user
        return userRepo.save(newUser);
    }

    public Principal login(NewLoginRequest req) {
        Optional<User> userOpt = userRepo.findByUsername(req.getUsername());

        if (userOpt.isPresent()) {
            User foundUser = userOpt.get();
            if (BCrypt.checkpw(req.getPassword(), foundUser.getPassword())) {
                return new Principal(foundUser);
            }
        }

        throw new UserNotFoundException("Invalid credential");
    }

    /**
     * Checks if the provided username is valid.
     *
     * @param username the username to validate
     * @return true if the username is valid, false otherwise
     */
    public boolean isValidUsername(String username) {
        return username.matches("^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$");
    }

    /**
     * Checks if the provided username is unique.
     *
     * @param username the username to check for uniqueness
     * @return true if the username is unique, false otherwise
     */
    public boolean isUniqueUsername(String username) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        return userOpt.isEmpty();
    }

    /**
     * Checks if the provided password is valid.
     *
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    public boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }

    /**
     * Checks if the provided password and confirm password match.
     *
     * @param password        the password to compare
     * @param confirmPassword the confirm password to compare
     * @return true if the passwords match, false otherwise
     */
    public boolean isSamePassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public String getUserId(String username) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        return userOpt.get().getId();
    }

    public Optional<User> findByUserName(String username) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        if(userOpt.isPresent()) {
            return userOpt;
        }
        throw new UserNotFoundException("User not found");
    }

    public String authenticateUser( String token ){
        jwtTokenService.isTokenExpired(token);
        String username = jwtTokenService.extractUsername(token);
        System.out.println("username " + username);
        Optional<User> userOpt = findByUserName(username);
        return username;
    }
 


}