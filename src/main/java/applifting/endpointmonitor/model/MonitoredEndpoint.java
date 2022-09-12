package applifting.endpointmonitor.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "MonitoredEndpoints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitoredEndpoint {

    @Id
    @Column(name = "id")
    @GenericGenerator(name="endpoint_id_increment" , strategy="increment")
    @GeneratedValue(generator="endpoint_id_increment")
    private Long id;

    @Column(name = "name")
    @Size(min = 3, max = 256, message = "Endpoint name must have between 3 and 256 characters including boundaries")
    @NotBlank(message = "Endpoint name cannot be blank")
    private String name;

    @Column(name = "url")
    @URL(message = "Provided URL is not valid")
    @NotBlank(message = "URL cannot be blank")
    private String url;

    @Column(name = "createdDate", updatable = false)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "lastCheckDate")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastCheckDate;

    @Column(name = "monitoringInterval")
    @Min(value = 1, message = "Monitoring interval cannot be less than 1 second")
    @NotNull(message = "Monitoring interval cannot be null")
    private Integer monitoringInterval;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "monitoredEndpoint", orphanRemoval = true)
    private List<MonitoringResult> monitoringResults;

}
