package pl.Pielichowski.bank_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.Pielichowski.bank_api.model.User;
import pl.Pielichowski.bank_api.service.UserService;

@RestController // Mówi Springowi: "To jest API, zwracaj wyniki w formacie JSON"
@RequestMapping("/api/users") // Każdy adres w tym pliku będzie zaczynał się od tego przedrostka
@Tag(name = "Users", description = "Rejestracja i odczyt klientów")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Lista użytkowników", description = "Zwraca użytkowników bez pól wrażliwych (hasło pomijane)")
    @GetMapping
    public java.util.List<pl.Pielichowski.bank_api.dto.UserDTO> getUsers() {
        return userService.getAllUsers().stream().map(user -> {
            pl.Pielichowski.bank_api.dto.UserDTO dto = new pl.Pielichowski.bank_api.dto.UserDTO();
            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Operation(summary = "Użytkownik po ID")
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Rejestracja użytkownika", description = "Hasło w żądaniu jawne; w odpowiedzi jest omitowane i w bazie przechowywane jako hash BCrypt")
    @PostMapping
    public User addUser(@jakarta.validation.Valid @RequestBody User user) {
        return userService.createUser(user);
    }
}