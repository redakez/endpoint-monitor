package applifting.endpointmonitor.service;

import applifting.endpointmonitor.dao.UserDao;
import applifting.endpointmonitor.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Implementation of {@link UserService}
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    private User findByIdOrElseThrowException(Long id) {
        String wasNotFoundMessage = "User with ID " + id + " was not found";
        return userDao.findById(id).orElseThrow(() -> {
            log.warn(wasNotFoundMessage);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, wasNotFoundMessage);
        });
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Retrieving all users");
        return userDao.findAll();
    }

    @Override
    public User getUserById(Long id) {
        log.info("Finding user by ID: {}", id);
        return findByIdOrElseThrowException(id);
    }

    @Override
    public User createUser(User user) {
        log.info("Creating new user with username: {}", user.getUsername());
        if (userDao.findByAccessToken(user.getAccessToken()).isPresent()) {
            log.info("Attempt to create a user with non-unique token ignored");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with such token already exists");
        }
        user.setId(null);
        user.setMonitoredEndpoints(null);
        return userDao.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        log.info("Updating user with ID: {}", id);
        User retrievedUser = findByIdOrElseThrowException(id);
        retrievedUser.setUsername(user.getUsername());
        retrievedUser.setEmail(user.getEmail());
        retrievedUser.setAccessToken(user.getAccessToken());
        return userDao.saveAndFlush(retrievedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        User user = findByIdOrElseThrowException(id);
        userDao.delete(user);
    }
}
