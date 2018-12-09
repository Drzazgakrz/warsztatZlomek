package pl.zlomek.warsztat.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@MappedSuperclass
@Getter
@Setter
public abstract class Account {

    @NotNull
    @Size(max = 30, min = 3)
    @Pattern(regexp = "[A-ZŹĄĘÓŁŻ]{1}+[a-z,ąęółńćźż]{2,}")
    @Column(name = "first_name")
    protected String firstName;

    @NotNull
    @Size(max = 30, min = 2)
    @Pattern(regexp = "[A-ZŹĄĘÓŁŻ]{1}+[a-z,ąęółńćźż]{2,}")
    @Column(name = "last_name")
    protected String lastName;

    @NotNull
    @Size(max=64, min = 64)
    protected String password;

    @Column(unique = true)
    @Pattern(regexp = "[A-Za-z0-9._-]{1,}+@+[a-z]{1,6}+.+[a-z]{2,3}")
    protected String email;

    @Column(name = "created_at")
    protected LocalDateTime createdAt;

    @Column(name = "last_logged_in")
    protected  LocalDateTime lastLoggedIn;

    public void setEmail(String email) {
        this.email = email;
    }

    public Account(String email, String firstname, String lastName, String password, LocalDateTime createdAt, LocalDateTime lastLoggedIn){
        this.email = email;
        this.firstName = firstname;
        this.lastName = lastName;
        this.password = hashPassword(password);
        this.lastLoggedIn = lastLoggedIn;
        this.createdAt = createdAt;
    }

    public static String hashPassword(String password){
        SHA3.DigestSHA3 sha3 = new SHA3.Digest256();
        byte[] digest = sha3.digest(password.getBytes());
        return Hex.toHexString(digest);
    }
}
