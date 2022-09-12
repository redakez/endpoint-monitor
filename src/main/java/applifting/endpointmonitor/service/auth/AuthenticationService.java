package applifting.endpointmonitor.service.auth;

import applifting.endpointmonitor.model.MonitoredEndpoint;
import applifting.endpointmonitor.model.User;
import org.springframework.web.server.ResponseStatusException;

/**
 * Interface for a service used to authenticate requests.
 */
public interface AuthenticationService {

    /**
     * Finds user by their access token and makes sure that the user exists.
     * @param accessToken access token by which the user should be found
     * @return user with the given access token
     * @throws ResponseStatusException when no user with the provided access token was found
     */
    User findUserAndAuthorizeByAccessToken(String accessToken) throws ResponseStatusException;

    /**
     * Finds a monitored endpoint by its ID and makes sure that it is owned by a user with the provided access token.
     * @param endpointId ID of the monitored endpoint to be found
     * @param accessToken access token of the user
     * @return monitored endpoint owned by user with the given access token
     * @throws ResponseStatusException if the user does not own the monitored endpoint or either the endpoint or the user do not exist
     */
    MonitoredEndpoint findEndpointAndAuthorizeByAccessToken(
            Long endpointId, String accessToken) throws ResponseStatusException;

}
