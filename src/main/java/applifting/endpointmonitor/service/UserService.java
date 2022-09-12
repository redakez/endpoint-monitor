package applifting.endpointmonitor.service;

import applifting.endpointmonitor.model.User;

import java.util.List;

/**
 * Interface for a service which manages objects of class {@link User}
 */
public interface UserService {

    /**
     * Returns all users in the system
     * @return a list of users
     */
    List<User> getAllUsers();

    /**
     * Returns a user with the specified ID
     * @param id ID of the user to be retrieved
     * @return a user
     */
    User getUserById(Long id);

    /**
     * Creates a new user
     * @param user user to be created
     * @return the created user
     */
    User createUser(User user);

    /**
     * Updates a user with the specified ID
     * @param id ID of the user to be updated
     * @param user specification of the new user
     * @return the updated user
     */
    User updateUser(Long id, User user);

    /**
     * Deletes a user with the specified ID
     * @param id ID of the user to be deleted
     */
    void deleteUser(Long id);

}
