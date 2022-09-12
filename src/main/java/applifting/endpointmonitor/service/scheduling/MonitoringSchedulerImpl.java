package applifting.endpointmonitor.service.scheduling;

import applifting.endpointmonitor.dao.MonitoredEndpointDao;
import applifting.endpointmonitor.dao.MonitoringResultDao;
import applifting.endpointmonitor.model.MonitoredEndpoint;
import applifting.endpointmonitor.model.MonitoringResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * Implementation of {@link MonitoringScheduler} using a {@link ScheduledExecutorService}
 */
@Slf4j
@Service
@Scope("singleton")
public class MonitoringSchedulerImpl implements MonitoringScheduler {

    @Autowired
    private MonitoredEndpointDao monitoredEndpointDao;

    @Autowired
    private MonitoringResultDao monitoringResultDao;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final ConcurrentHashMap<Long, ScheduledFuture<?>> monitorMap = new ConcurrentHashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    private void activateMonitorsAfterStartup() {
        log.info("Starting background monitors for monitored endpoints currently present in the database");
        List<MonitoredEndpoint> monitoredEndpoints = monitoredEndpointDao.findAll();
        for (MonitoredEndpoint me: monitoredEndpoints) {
            this.addMonitor(me);
        }
        log.info("All background monitors are running");
    }

    private void checkEndpoint(MonitoredEndpoint monitoredEndpoint) {
        log.info("Checking endpoint with ID: {} and URL: {}", monitoredEndpoint.getId(), monitoredEndpoint.getUrl());
        RestTemplate restTemplate = new RestTemplate();
        String body = null;
        Integer statusCode = null;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(monitoredEndpoint.getUrl(), String.class);
            body = response.getBody();
            statusCode = response.getStatusCodeValue();
        } catch (RestClientResponseException e) { //Site did not return a 2xx status code
            statusCode = e.getRawStatusCode();
            body = e.getResponseBodyAsString();
        } catch (ResourceAccessException e) { //Site is not reachable
            statusCode = -1;
        } catch (Exception e) { //Unknown error
            log.error("An unknown error occurred while checking URL: {}, stack trace: {}",
                    monitoredEndpoint.getId(), e.getStackTrace());
            return;
        }
        log.info("Finished checking URL: {}, received status code: {}", monitoredEndpoint.getUrl(), statusCode);
        if (body != null && body.length() > 100000) {
            log.info("Retrieved payload exceeds maximum payload size, truncating the payload");
            body = body.substring(0, 100000);
        }
        monitoringResultDao.save(new MonitoringResult(null, new Date(), statusCode, body, monitoredEndpoint));
        monitoredEndpointDao.updateCheckDateById(monitoredEndpoint.getId());
    }

    @Override
    public void addMonitor(MonitoredEndpoint monitoredEndpoint) {
        log.info("Adding new monitor for endpoint with ID: {} and URL: {}",
                monitoredEndpoint.getId(), monitoredEndpoint.getUrl());
        if (monitorMap.containsKey(monitoredEndpoint.getId())) {
            log.warn("An attempt to add monitor for already existing endpoint was ignored");
            return;
        }
        ScheduledFuture<?> future = executorService.scheduleWithFixedDelay(
                () -> checkEndpoint(monitoredEndpoint),
                0,
                monitoredEndpoint.getMonitoringInterval(),
                TimeUnit.SECONDS);
        monitorMap.put(monitoredEndpoint.getId(), future);
    }

    @Override
    public void updateMonitor(MonitoredEndpoint monitoredEndpoint) {
        this.removeMonitor(monitoredEndpoint);
        this.addMonitor(monitoredEndpoint);
    }

    @Override
    public void removeMonitor(MonitoredEndpoint monitoredEndpoint) {
        log.info("Removing monitor for endpoint with ID: {} and URL: {}",
                monitoredEndpoint.getId(), monitoredEndpoint.getUrl());
        ScheduledFuture<?> future = monitorMap.remove(monitoredEndpoint.getId());
        if (future == null) {
            log.warn("An attempt to remove non-existing monitor was ignored");
            return;
        }
        future.cancel(false);
    }
}
