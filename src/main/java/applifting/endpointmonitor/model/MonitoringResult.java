package applifting.endpointmonitor.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "MonitoringResults")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringResult {

    @Id
    @Column(name = "id")
    @GenericGenerator(name="result_id_increment" , strategy="increment")
    @GeneratedValue(generator="result_id_increment")
    private Long id;

    @Column(name = "timeOfCheck")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date timeOfCheck;

    @Column(name = "returnedHttpCode")
    private Integer returnedHttpCode;

    @Column(name = "returnedPayload", length = 100000)
    private String returnedPayload;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private MonitoredEndpoint monitoredEndpoint;

}
