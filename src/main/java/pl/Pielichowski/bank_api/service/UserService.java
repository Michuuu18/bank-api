package pl.Pielichowski.bank_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import pl.Pielichowski.bank_api.model.User;
import pl.Pielichowski.bank_api.repository.UserRepository;

@Service // Komunikat dla Springa: "Tu jest logika biznesowa"
public class UserService {

    private final UserRepository userRepository;

    // Wstrzykiwanie zależności przez konstruktor - to standard rynkowy (tzw. Dependency Injection)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    // Metoda zapisująca nowego klienta do bazy
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
