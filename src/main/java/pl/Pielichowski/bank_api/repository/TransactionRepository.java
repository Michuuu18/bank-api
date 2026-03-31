package pl.Pielichowski.bank_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.Pielichowski.bank_api.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}