package applifting.endpointmonitor.service.scheduling;

import applifting.endpointmonitor.model.MonitoredEndpoint;

/**
 * Interface for a service which periodically checks given URLs.
 * The results are saved into a database.
 */
public interface MonitoringScheduler {

    /**
     * Adds a new endpoint to be monitored
     * @param monitoredEndpoint specification of the endpoint to be monitored
     */
    void addMonitor(MonitoredEndpoint monitoredEndpoint);

    /**
     * Updates a specified monitor
     * @param monitoredEndpoint specification of the endpoint to be updated
     */
    void updateMonitor(MonitoredEndpoint monitoredEndpoint);

    /**
     * Removes an endpoint monitor
     * @param monitoredEndpoint specification of the endpoint that should be removed
     */
    void removeMonitor(MonitoredEndpoint monitoredEndpoint);

}
