package pl.Pielichowski.bank_api.controller;

import org.springframework.web.bind.annotation.*;
import pl.Pielichowski.bank_api.model.Account;
import pl.Pielichowski.bank_api.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> getAccounts() {
        return accountService.getAllAccounts();
    }

    @PostMapping
    public Account addAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }
}