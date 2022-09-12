package applifting.endpointmonitor.service;

import applifting.endpointmonitor.dao.MonitoredEndpointDao;
import applifting.endpointmonitor.dao.UserDao;
import applifting.endpointmonitor.model.MonitoredEndpoint;
import applifting.endpointmonitor.model.User;
import applifting.endpointmonitor.service.auth.AuthenticationService;
import applifting.endpointmonitor.service.scheduling.MonitoringScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link MonitoredEndpointService}
 */
@Service
@Slf4j
public class MonitoredEndpointServiceImpl implements MonitoredEndpointService {
    @Autowired
    private MonitoredEndpointDao monitoredEndpointDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MonitoringScheduler monitoringScheduler;

    @Override
    public List<MonitoredEndpoint> getAllEndpointsWithAccessToken(String accessToken) {
        log.info("Retrieving all endpoints");
        User user = authenticationService.findUserAndAuthorizeByAccessToken(accessToken);
        return user.getMonitoredEndpoints();
    }

    @Override
    public MonitoredEndpoint getEndpointById(Long id, String accessToken) {
        log.info("Retrieving endpoint with ID: {}", id);
        return authenticationService.findEndpointAndAuthorizeByAccessToken(id, accessToken);
    }

    @Override
    public MonitoredEndpoint createEndpoint(MonitoredEndpoint monitoredEndpoint, String accessToken) {
        log.info("Creating new endpoint with URL: {}", monitoredEndpoint.getUrl());
        User user = authenticationService.findUserAndAuthorizeByAccessToken(accessToken);
        monitoredEndpoint.setId(null);
        monitoredEndpoint.setMonitoringResults(null);
        monitoredEndpoint.setOwner(user);
        MonitoredEndpoint createdEndpoint = monitoredEndpointDao.save(monitoredEndpoint);
        user.addMonitoredEndpoint(createdEndpoint);
        userDao.saveAndFlush(user);
        monitoringScheduler.addMonitor(monitoredEndpoint);
        return createdEndpoint;
    }

    @Override
    public MonitoredEndpoint updateEndpoint(Long id, MonitoredEndpoint monitoredEndpoint, String accessToken) {
        log.info("Updating endpoint with ID: {}", monitoredEndpoint.getId());
        MonitoredEndpoint retrievedEndpoint =
                authenticationService.findEndpointAndAuthorizeByAccessToken(id, accessToken);
        retrievedEndpoint.setName(monitoredEndpoint.getName());
        retrievedEndpoint.setUrl(monitoredEndpoint.getUrl());
        retrievedEndpoint.setMonitoringInterval(monitoredEndpoint.getMonitoringInterval());
        MonitoredEndpoint updatedEndpoint = monitoredEndpointDao.saveAndFlush(retrievedEndpoint);
        monitoringScheduler.updateMonitor(updatedEndpoint);
        return updatedEndpoint;
    }

    @Override
    public void deleteEndpoint(Long id, String accessToken) {
        log.info("Deleting endpoint with ID: {}", id);
        MonitoredEndpoint monitoredEndpoint =
                authenticationService.findEndpointAndAuthorizeByAccessToken(id, accessToken);
        monitoringScheduler.removeMonitor(monitoredEndpoint);
        monitoredEndpointDao.delete(monitoredEndpoint);
    }
}
