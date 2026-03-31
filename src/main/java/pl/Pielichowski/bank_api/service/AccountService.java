package pl.Pielichowski.bank_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.Pielichowski.bank_api.model.Account;
import pl.Pielichowski.bank_api.model.Transaction;
import pl.Pielichowski.bank_api.repository.AccountRepository;
import pl.Pielichowski.bank_api.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    // To jest konstruktor - Spring sam "wstrzyknie" tu oba repozytoria
    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
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

    @Transactional
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount, String title) {
        Account fromAccount = getAccountById(fromAccountId);
        Account toAccount = getAccountById(toAccountId);

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Niewystarczające środki na koncie!");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Tworzymy ślad w historii
        Transaction transaction = new Transaction();
        transaction.setSourceAccountId(fromAccountId);
        transaction.setTargetAccountId(toAccountId);
        transaction.setAmount(amount);
        transaction.setTitle(title);
        transaction.setTimestamp(LocalDateTime.now());
        
        transactionRepository.save(transaction);
    }
}