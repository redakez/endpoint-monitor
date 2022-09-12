package applifting.endpointmonitor.service;

import applifting.endpointmonitor.model.MonitoringResult;

import java.util.List;

/**
 * Interface for a service which manages objects of class {@link MonitoringResult}
 */
public interface MonitoringResultService {

    /**
     * Retrieves results of a monitored endpoint
     * @param endpointId ID of the endpoint which results should be retrieved
     * @param accessToken access token of a user
     * @return a list of monitoring results
     */
    List<MonitoringResult> getAllResultsForEndpoint(Long endpointId, String accessToken);

    /**
     * Retrieves results of a monitored endpoint
     * @param endpointId ID of the endpoint which results should be retrieved
     * @param accessToken access token of a user
     * @param numberOfItems number of items to be retrieved
     * @return a list of monitoring results
     */
    List<MonitoringResult> getLastNResultsForEndpoint(Long endpointId, Integer numberOfItems, String accessToken);

}
