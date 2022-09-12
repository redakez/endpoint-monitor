package applifting.endpointmonitor.dao;

import applifting.endpointmonitor.model.MonitoredEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * JpaRepository for the objects of class {@link MonitoredEndpoint}
 */
public interface MonitoredEndpointDao extends JpaRepository<MonitoredEndpoint, Long> {

    @Transactional
    @Modifying
    @Query("update MonitoredEndpoint me set me.lastCheckDate = CURRENT_TIMESTAMP where me.id = ?1")
    void updateCheckDateById(Long id);

}
