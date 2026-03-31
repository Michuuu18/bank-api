package pl.Pielichowski.bank_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pl.Pielichowski.bank_api.model.User;
import pl.Pielichowski.bank_api.service.UserService;

@RestController // Mówi Springowi: "To jest API, zwracaj wyniki w formacie JSON"
@RequestMapping("/api/users") // Każdy adres w tym pliku będzie zaczynał się od tego przedrostka
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint do pobierania listy klientów. 
    // Dostępny pod adresem: GET http://localhost:8080/api/users
    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
// Ścieżka będzie wyglądać tak: GET http://localhost:8081/api/users/1
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    // Endpoint do tworzenia nowego klienta z walidacją danych
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }
}