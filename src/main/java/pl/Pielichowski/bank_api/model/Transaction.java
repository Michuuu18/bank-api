package pl.Pielichowski.bank_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sourceAccountId; // ID konta nadawcy

    @Column(nullable = false)
    private Long targetAccountId; // ID konta odbiorcy

    @Column(nullable = false)
    private BigDecimal amount; // Kwota

    @Column(nullable = false)
    private String title; // Tytul przelewu

    @Column(nullable = false)
    private LocalDateTime timestamp; // Data i godzina
}