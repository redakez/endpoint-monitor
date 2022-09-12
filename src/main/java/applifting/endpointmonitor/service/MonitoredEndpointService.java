package applifting.endpointmonitor.service;

import applifting.endpointmonitor.model.MonitoredEndpoint;

import java.util.List;

/**
 * Interface for a service which manages objects of class {@link MonitoredEndpoint}
 */
public interface MonitoredEndpointService {

    /**
     * Returns all endpoints owned by a user. The user is specified by their access token.
     * @param accessToken access token of a user
     * @return a list of endpoints owned by a specific user
     */
    List<MonitoredEndpoint> getAllEndpointsWithAccessToken(String accessToken);

    /**
     * Returns an endpoint with a specific ID.
     * @param id ID of the endpoint to be retrieved
     * @param accessToken access token of a user
     * @return an endpoint with a specific ID
     */
    MonitoredEndpoint getEndpointById(Long id, String accessToken);

    /**
     * Creates a new endpoint.
     * @param monitoredEndpoint monitored endpoint to be created
     * @param accessToken access token of the user which will become owner of the monitored endpoint
     * @return the created endpoint
     */
    MonitoredEndpoint createEndpoint(MonitoredEndpoint monitoredEndpoint, String accessToken);

    /**
     * Updates an endpoint with a specific ID.
     * @param id ID of the endpoint to be updated
     * @param monitoredEndpoint specification of the new monitored endpoint
     * @param accessToken access token of a user
     * @return the updated endpoint
     */
    MonitoredEndpoint updateEndpoint(Long id, MonitoredEndpoint monitoredEndpoint, String accessToken);

    /**
     * Deletes an endpoint with a specific ID.
     * @param id ID of the endpoint to be deleted
     * @param accessToken access token of a user
     */
    void deleteEndpoint(Long id, String accessToken);
}
