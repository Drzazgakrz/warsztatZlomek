package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employee_token")
public class EmployeeToken extends AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @ManyToOne
    private Employee employee;

    public EmployeeToken(String accessToken, LocalDateTime expiration, Employee employee) {
        super(accessToken, expiration);
        this.employee = employee;
    }
}
