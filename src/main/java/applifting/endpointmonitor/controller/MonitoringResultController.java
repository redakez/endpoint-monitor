package applifting.endpointmonitor.controller;

import applifting.endpointmonitor.model.MonitoringResult;
import applifting.endpointmonitor.service.MonitoringResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for the objects of class {@link MonitoringResult}
 */
@RestController
public class MonitoringResultController {

    private static final String controllerPath = "/endpoints/{id}/results";

    @Autowired
    private MonitoringResultService monitoringResultService;

    /**
     * Retrieves results of a monitored endpoint
     * @param id ID of the endpoint which results should be retrieved
     * @param accessToken access token of a user, specified in the header
     * @param numberOfItems number of items to be retrieved, if it is not specified then all items are retrieved
     * @return a list of monitoring results wrapped in a {@link ResponseEntity} class
     */
    @GetMapping(controllerPath)
    public ResponseEntity<List<MonitoringResult>> getResults(
                @PathVariable Long id,
                @RequestHeader(value = "accessToken") String accessToken,
                @RequestParam(value = "count", required = false) Integer numberOfItems) {
        if (numberOfItems == null) {
            return ResponseEntity.ok(monitoringResultService.getAllResultsForEndpoint(id, accessToken));
        } else {
            return ResponseEntity.ok(monitoringResultService.getLastNResultsForEndpoint(id, numberOfItems, accessToken));
        }
    }

}
