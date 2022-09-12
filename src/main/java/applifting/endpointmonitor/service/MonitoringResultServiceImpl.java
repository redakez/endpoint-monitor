package applifting.endpointmonitor.service;

import applifting.endpointmonitor.dao.MonitoredEndpointDao;
import applifting.endpointmonitor.dao.MonitoringResultDao;
import applifting.endpointmonitor.model.MonitoredEndpoint;
import applifting.endpointmonitor.model.MonitoringResult;
import applifting.endpointmonitor.service.auth.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link MonitoringResultService}
 */
@Service
@Slf4j
public class MonitoringResultServiceImpl implements MonitoringResultService {

    @Autowired
    private MonitoringResultDao monitoringResultDao;

    @Autowired
    private MonitoredEndpointDao monitoredEndpointDao;

    @Autowired
    private AuthenticationService authenticationService;

    private List<MonitoringResult> getAllSorted(Long endpointId, String accessToken) {
        MonitoredEndpoint me = authenticationService.findEndpointAndAuthorizeByAccessToken(endpointId, accessToken);
        List<MonitoringResult> sortedResults = me.getMonitoringResults();
        sortedResults.sort(Comparator.comparing(MonitoringResult::getTimeOfCheck));
        Collections.reverse(sortedResults);
        return sortedResults;
    }

    @Override
    public List<MonitoringResult> getAllResultsForEndpoint(Long endpointId, String accessToken) {
        log.info("Retrieving all monitoring results for endpoint with ID: {}", endpointId);
        return getAllSorted(endpointId, accessToken);
    }

    @Override
    public List<MonitoringResult> getLastNResultsForEndpoint(Long endpointId, Integer numberOfItems, String accessToken) {
        log.info("Retrieving last {} monitoring results for endpoint with ID: {}", numberOfItems, endpointId);
        return getAllSorted(endpointId, accessToken).stream()
                .limit(Math.max(0, numberOfItems))
                .collect(Collectors.toList());
    }

}