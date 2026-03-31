package pl.Pielichowski.bank_api.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pl.Pielichowski.bank_api.model.User;
import pl.Pielichowski.bank_api.repository.UserRepository;

@Service // Komunikat dla Springa: "Tu jest logika biznesowa"
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Metoda pobierająca wszystkich klientów z bazy
    public List<User> getAllUsers() {
        return userRepository.findAll(); // Ta metoda to darmowy prezent od JpaRepository!
    }
    public User getUserById(Long id) {
        // findById zwraca tzw. Optional (pudełko, które może być puste, jeśli w bazie nie ma takiego ID)
        // Jeśli jest puste, rzucamy błędem, którym zajmiemy się w kolejnym tasku!
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika o ID: " + id));
    }

    // Metoda zapisująca nowego klienta do bazy (hasło zapisywane jako hash BCrypt)
    public User createUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
}
