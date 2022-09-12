package applifting.endpointmonitor.controller;

import applifting.endpointmonitor.model.MonitoredEndpoint;
import applifting.endpointmonitor.service.MonitoredEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for the objects of class {@link MonitoredEndpoint}
 */
@RestController
public class MonitoredEndpointController {

    private static final String controllerPath = "/endpoints";

    @Autowired
    private MonitoredEndpointService monitoredEndpointService;

    /**
     * Returns all endpoints owned by a user. The user is specified by their access token.
     * @param accessToken access token of a user, specified in the header
     * @return a list of endpoints owned by a specific user wrapped in a {@link ResponseEntity} class
     */
    @GetMapping(controllerPath)
    public ResponseEntity<List<MonitoredEndpoint>> getEndpointsOfUser(
                @RequestHeader(value = "accessToken") String accessToken) {
        return ResponseEntity.ok(monitoredEndpointService.getAllEndpointsWithAccessToken(accessToken));
    }

    /**
     * Returns an endpoint with a specific ID.
     * @param id ID of the endpoint to be retrieved
     * @param accessToken access token of a user, specified in the header
     * @return an endpoint with a specific ID wrapped in a {@link ResponseEntity} class
     */
    @GetMapping(controllerPath + "/{id}")
    public ResponseEntity<MonitoredEndpoint> getEndpoint(@PathVariable Long id,
                                                         @RequestHeader(value = "accessToken") String accessToken) {
        return ResponseEntity.ok(monitoredEndpointService.getEndpointById(id, accessToken));
    }

    /**
     * Creates a new endpoint.
     * @param monitoredEndpoint monitored endpoint to be created
     * @param accessToken access token of the user which will become owner of the endpoint, specified in the header
     * @return the created endpoint wrapped in a {@link ResponseEntity} class
     */
    @PostMapping(controllerPath)
    public ResponseEntity<MonitoredEndpoint> postEndpoint(@RequestBody @Valid MonitoredEndpoint monitoredEndpoint,
                                                          @RequestHeader(value = "accessToken") String accessToken) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                monitoredEndpointService.createEndpoint(monitoredEndpoint, accessToken));
    }

    /**
     * Updates an endpoint with a specific ID.
     * @param id ID of the endpoint to be updated
     * @param monitoredEndpoint specification of the new monitored endpoint
     * @param accessToken access token of a user, specified in the header
     * @return the updated endpoint wrapped in a {@link ResponseEntity} class
     */
    @PutMapping(controllerPath + "/{id}")
    public ResponseEntity<MonitoredEndpoint> putEndpoint(@PathVariable Long id,
                                                         @RequestBody @Valid MonitoredEndpoint monitoredEndpoint,
                                                         @RequestHeader(value = "accessToken") String accessToken) {
        return ResponseEntity.status(HttpStatus.OK).body(
                monitoredEndpointService.updateEndpoint(id, monitoredEndpoint, accessToken));
    }


    /**
     * Deletes an endpoint with a specific ID.
     * @param id ID of the endpoint to be deleted
     * @param accessToken access token of a user, specified in the header
     * @return {@link ResponseEntity} with an empty body
     */
    @DeleteMapping(controllerPath + "/{id}")
    public ResponseEntity<?> deleteEndpoint(@PathVariable Long id,
                                         @RequestHeader(value = "accessToken") String accessToken) {
        monitoredEndpointService.deleteEndpoint(id, accessToken);
        return ResponseEntity.ok().build();
    }

}
