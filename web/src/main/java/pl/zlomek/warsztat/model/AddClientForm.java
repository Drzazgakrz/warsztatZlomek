package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddClientForm {
    private String username;
    private String companyName;
    private String employeeToken;
}
