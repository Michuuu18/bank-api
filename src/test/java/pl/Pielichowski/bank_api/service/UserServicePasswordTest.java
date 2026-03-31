package pl.Pielichowski.bank_api.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import pl.Pielichowski.bank_api.model.User;

@SpringBootTest
@Transactional
class UserServicePasswordTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createUser_storesPasswordAsBCryptHash() {
        User user = new User();
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setEmail("jan.kowalski+bcrypt@test.local");
        user.setPassword("secret12");

        User saved = userService.createUser(user);

        assertThat(saved.getPassword()).startsWith("$2");
        assertThat(passwordEncoder.matches("secret12", saved.getPassword())).isTrue();
    }
}
