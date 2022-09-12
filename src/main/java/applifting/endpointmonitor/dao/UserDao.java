package applifting.endpointmonitor.dao;

import applifting.endpointmonitor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JpaRepository for the objects of class {@link User}
 */
public interface UserDao extends JpaRepository<User, Long> {
    Optional<User> findByAccessToken(String accessToken);

}
