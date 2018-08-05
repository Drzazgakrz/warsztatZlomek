package pl.zlomek.warsztat.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Setter
@lombok.Getter
@lombok.ToString
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long clientId;

    @NotNull
    @Size(max = 30, min = 3)
    private String firstName;

    @NotNull
    @Size(max = 30, min = 2)
    private String lastName;

    @NotNull
    @Size(max = 30, min = 6)
    private String email;

    @NotNull
    @Size(max = 15, min = 9)
    private String phoneNumber;

    @NotNull
    @Size(max = 20, min = 2)
    private String cityName;

    @NotNull
    @Size(max = 40, min = 3)
    private String streetName;

    @NotNull
    @Size(max = 5, min = 1)
    private String buildNum;

    @Size(max = 5)
    private String aptNum;

    @NotNull
    @Size(max = 6, min = 6)
    private String zipCode;

    @NotNull
    @Size(max=256, min = 256)
    private String password;

    @ManyToMany(mappedBy = "employees")
    private List<Company> companies;
}
