package applifting.endpointmonitor.controller;

import applifting.endpointmonitor.model.User;
import applifting.endpointmonitor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for the objects of class {@link User}
 */
@RestController
public class UserController {

    private static final String controllerPath = "/users";

    @Autowired
    private UserService userService;

    /**
     * Returns all users in the system
     * @return a list of users wrapped in a {@link ResponseEntity} class
     */
    @GetMapping(controllerPath)
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Returns a user with the specified ID
     * @param id ID of the user to be retrieved
     * @return a user wrapped in a {@link ResponseEntity} class
     */
    @GetMapping(controllerPath + "/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Creates a new user
     * @param user user to be created
     * @return the created user wrapped in a {@link ResponseEntity} class
     */
    @PostMapping(controllerPath)
    public ResponseEntity<User> postUser(@RequestBody @Valid User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
    }

    /**
     * Updates a user with the specified ID
     * @param id ID of the user to be updated
     * @param user specification of the new user
     * @return the updated user wrapped in a {@link ResponseEntity} class
     */
    @PutMapping(controllerPath + "/{id}")
    public ResponseEntity<User> putUser(@PathVariable Long id, @RequestBody @Valid User user) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(id, user));
    }

    /**
     * Deletes a user with the specified ID
     * @param id ID of the user to be deleted
     * @return {@link ResponseEntity} with an empty body
     */
    @DeleteMapping(controllerPath + "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

}
