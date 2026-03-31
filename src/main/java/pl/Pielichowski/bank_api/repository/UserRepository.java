package pl.Pielichowski.bank_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.Pielichowski.bank_api.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Rozszerzając JpaRepository dostajemy za darmo wszystkie podstawowe operacje na bazie!
    // User - to nasza encja, Long - to typ naszego klucza głównego (ID).
}