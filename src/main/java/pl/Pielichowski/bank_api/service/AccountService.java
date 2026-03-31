package pl.Pielichowski.bank_api.service;

import org.springframework.stereotype.Service;
import pl.Pielichowski.bank_api.model.Account;
import pl.Pielichowski.bank_api.repository.AccountRepository;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono konta o ID: " + id));
    }
}