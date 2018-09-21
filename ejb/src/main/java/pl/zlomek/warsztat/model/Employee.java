package pl.zlomek.warsztat.model;

import lombok.NoArgsConstructor;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
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
    private Date hireDate;

    @Column(name = "quit_date")
    private Date quitDate;

    @NotNull
    private EmployeeStatus status;

    @OneToMany (mappedBy = "employee")
    private Set<Visit> visits;

    public Employee(String firstName, String lastName, Date hireDate, Date quitDate, String password, String email,
                    EmployeeStatus status){
        super(email, firstName, lastName, password);
        this.hireDate = hireDate;
        this.quitDate = quitDate;
        this.status = status;
    }
}
