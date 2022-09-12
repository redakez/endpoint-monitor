package applifting.endpointmonitor.dao;

import applifting.endpointmonitor.model.MonitoringResult;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository for the objects of class {@link MonitoringResult}
 */
public interface MonitoringResultDao extends JpaRepository<MonitoringResult, Long> {

}
