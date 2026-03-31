package pl.Pielichowski.bank_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.Pielichowski.bank_api.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}