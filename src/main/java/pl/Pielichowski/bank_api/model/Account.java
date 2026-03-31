package pl.Pielichowski.bank_api.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountNumber;
    
    // WAŻNE: W bankowości do pieniędzy ZAWSZE używamy BigDecimal, nigdy double!
    @Column(nullable = false)
    private BigDecimal balance;
    
    @Column(nullable = false)
    private String currency; // np. "PLN", "EUR"

    // Relacja bazy danych: Wiele kont może należeć do jednego użytkownika
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}