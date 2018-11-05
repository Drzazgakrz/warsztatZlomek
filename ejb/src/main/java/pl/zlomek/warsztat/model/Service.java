package pl.zlomek.warsztat.model;


import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@lombok.AllArgsConstructor
@lombok.Setter
@lombok.Getter
@NoArgsConstructor
@Entity
@Table(name = "services")
public class Service implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "service_name")
    @NotNull
    @Pattern(regexp = "[A-ZŹĄĘÓŁŻ]{1}+[a-z,ąęółńćźż]{2,}")
    private String name;

    @OneToMany(mappedBy = "service")
    @NotNull
    private Set<VisitsHasServices> visits;

    private int tax;

    public Service(String name, int tax) {
        this.name = name;
        visits = new HashSet<>();
        this.tax = tax;
    }
}
