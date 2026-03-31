package pl.Pielichowski.bank_api.controller;

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
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

   // Endpoint do pobierania bezpiecznej listy klientów (bez haseł)
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

    // Ścieżka do pobierania pojedynczego użytkownika
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Endpoint do tworzenia nowego klienta z walidacją
    @PostMapping
    public User addUser(@jakarta.validation.Valid @RequestBody User user) {
        return userService.createUser(user);
    }
}