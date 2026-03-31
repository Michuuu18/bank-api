package pl.Pielichowski.bank_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sourceAccountId; // ID konta nadawcy
    private Long targetAccountId; // ID konta odbiorcy
    private BigDecimal amount;     // Kwota
    private String title;          // Tytuł przelewu
    private LocalDateTime timestamp; // Data i godzina
}