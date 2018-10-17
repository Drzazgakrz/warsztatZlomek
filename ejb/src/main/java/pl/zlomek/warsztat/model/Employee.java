package pl.zlomek.warsztat.model;

import lombok.NoArgsConstructor;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@lombok.AllArgsConstructor
@lombok.Setter
@lombok.Getter
@NoArgsConstructor
@Entity
@Table(name =  "employees")
public class Employee extends Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "quit_date")
    private LocalDate quitDate;

    @NotNull
    private EmployeeStatus status;

    @OneToMany (mappedBy = "employee")
    private Set<Visit> visits;

    @OneToMany(mappedBy = "employee")
    private Set<EmployeeToken> accessToken;

    public Employee(String firstName, String lastName, LocalDate hireDate, LocalDate quitDate, String password, String email,
                    EmployeeStatus status){
        super(email, firstName, lastName, password, LocalDateTime.now(), LocalDateTime.now());
        this.hireDate = hireDate;
        this.quitDate = quitDate;
        this.status = status;
        accessToken = new HashSet<>();
    }
}
