package pl.zlomek.warsztat.model;

//Gettery i settery. WAŻNE!
@lombok.Getter
@lombok.Setter
public class RegisterForm {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String cityName;
    private String streetName;
    private String buildNum;
    private String aptNum;
    private String zipCode;
    private String password;
    private String confirmPassword;
}
