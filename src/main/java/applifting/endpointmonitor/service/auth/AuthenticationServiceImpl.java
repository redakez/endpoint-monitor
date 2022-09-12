package applifting.endpointmonitor.service.auth;


import applifting.endpointmonitor.dao.MonitoredEndpointDao;
import applifting.endpointmonitor.dao.MonitoringResultDao;
import applifting.endpointmonitor.dao.UserDao;
import applifting.endpointmonitor.model.MonitoredEndpoint;
import applifting.endpointmonitor.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Implementation of {@link AuthenticationService}.
 */
@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final static String unauthorizedMessage = "You are unauthorized for this operation";

    @Autowired
    private UserDao userDao;

    @Autowired
    private MonitoredEndpointDao monitoredEndpointDao;

    @Autowired
    private MonitoringResultDao monitoringResultDao;

    @Override
    public User findUserAndAuthorizeByAccessToken(String accessToken) throws ResponseStatusException {
        return userDao.findByAccessToken(accessToken).orElseThrow(() -> {
            log.info("User could not be found by access token");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorizedMessage);
        });
    }

    @Override
    public MonitoredEndpoint findEndpointAndAuthorizeByAccessToken(
            Long endpointId, String accessToken) throws ResponseStatusException {
        Optional<MonitoredEndpoint> endpoint = monitoredEndpointDao.findById(endpointId);
        if (endpoint.isEmpty() || !endpoint.get().getOwner().getAccessToken().equals(accessToken)) {
            log.info("Access was denied to to endpoint with ID: {}", endpointId);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorizedMessage);
        }
        return endpoint.get();
    }

}
