package pl.zlomek.warsztat.model;

import lombok.NoArgsConstructor;
import org.bouncycastle.jcajce.provider.digest.SHA3;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@lombok.AllArgsConstructor
@lombok.Setter
@lombok.Getter
@NoArgsConstructor
@Entity
@Table(name =  "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Column(name = "hire_date")
    private Date hireDate;

    @Column(name = "quit_date")
    private Date quitDate;

    @NotNull
    private String password;

    @NotNull
    private String email;

    @NotNull
    private EmployeeStatus status;

    public Employee(String firstName, String lastName, Date hireDate, Date quitDate, String password, String email,
                    EmployeeStatus status){
        this.firstName = firstName;
        this.lastName = lastName;
        this.hireDate = hireDate;
        this.quitDate = quitDate;
        SHA3.DigestSHA3 sha3 = new SHA3.Digest256();
        sha3.update(password.getBytes());
        this.password = sha3.digest().toString();
        this.email = email;
        this.status = status;
    }
}
