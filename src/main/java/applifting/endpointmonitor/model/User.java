package applifting.endpointmonitor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "id")
    @GenericGenerator(name="user_id_increment" , strategy="increment")
    @GeneratedValue(generator="user_id_increment")
    private Long id;

    @Column(name = "username")
    @Size(min = 3, max = 256, message = "Username must have between 3 and 256 characters including boundaries")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Column(name = "email")
    @Email(message = "Email is not valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Column(name = "accessToken", unique = true)
    @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
            message = "Provided access token is not in a UUID4 format")
    @NotBlank(message = "Access token cannot be blank")
    private String accessToken;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    private List<MonitoredEndpoint> monitoredEndpoints;

    public void addMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint) {
        if (this.monitoredEndpoints == null) {
            this.monitoredEndpoints = new ArrayList<>();
        }
        this.monitoredEndpoints.add(monitoredEndpoint);
    }

}
