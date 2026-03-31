package pl.Pielichowski.bank_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue; // Ważny import!
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Imię nie może być puste")
    private String firstName;

    @NotBlank(message = "Nazwisko nie może być puste")
    private String lastName;

    @Email(message = "Błędny format adresu e-mail")
    @NotBlank(message = "Email jest wymagany")
    private String email;

    @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków")
    private String password;
}