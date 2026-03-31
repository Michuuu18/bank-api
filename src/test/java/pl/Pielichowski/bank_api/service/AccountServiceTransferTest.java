package pl.Pielichowski.bank_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.Pielichowski.bank_api.model.Account;
import pl.Pielichowski.bank_api.model.Transaction;
import pl.Pielichowski.bank_api.model.User;
import pl.Pielichowski.bank_api.repository.AccountRepository;
import pl.Pielichowski.bank_api.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService — logika przelewów")
class AccountServiceTransferTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    private User sampleUser;
    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1L, "Jan", "Kowalski", "jan@example.com", "secret");
        fromAccount = new Account(10L, "PL001", new BigDecimal("100.00"), "PLN", sampleUser);
        toAccount = new Account(20L, "PL002", new BigDecimal("50.00"), "PLN", sampleUser);
        lenient().when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Nested
    @DisplayName("transferMoney — scenariusze poprawne")
    class SuccessfulTransfers {

        @Test
        @DisplayName("aktualizuje salda i zapisuje transakcję z poprawnymi polami")
        void updatesBalancesAndSavesTransaction() {
            when(accountRepository.findById(10L)).thenReturn(Optional.of(fromAccount));
            when(accountRepository.findById(20L)).thenReturn(Optional.of(toAccount));

            accountService.transferMoney(10L, 20L, new BigDecimal("30.00"), "Czynsz");

            assertThat(fromAccount.getBalance()).isEqualByComparingTo("70.00");
            assertThat(toAccount.getBalance()).isEqualByComparingTo("80.00");

            verify(accountRepository, times(2)).save(any(Account.class));

            ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
            verify(transactionRepository).save(txCaptor.capture());
            Transaction saved = txCaptor.getValue();
            assertThat(saved.getSourceAccountId()).isEqualTo(10L);
            assertThat(saved.getTargetAccountId()).isEqualTo(20L);
            assertThat(saved.getAmount()).isEqualByComparingTo("30.00");
            assertThat(saved.getTitle()).isEqualTo("Czynsz");
            assertThat(saved.getTimestamp()).isNotNull();
            assertThat(saved.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now().plusSeconds(1));
        }

        @Test
        @DisplayName("przelew na kwotę równą całemu saldu nadawcy jest dozwolony")
        void allowsTransferEqualToFullBalance() {
            when(accountRepository.findById(10L)).thenReturn(Optional.of(fromAccount));
            when(accountRepository.findById(20L)).thenReturn(Optional.of(toAccount));

            accountService.transferMoney(10L, 20L, new BigDecimal("100.00"), "Wypłata");

            assertThat(fromAccount.getBalance()).isEqualByComparingTo("0.00");
            assertThat(toAccount.getBalance()).isEqualByComparingTo("150.00");
            verify(transactionRepository).save(any(Transaction.class));
        }
    }

    @Nested
    @DisplayName("transferMoney — błędy")
    class FailureCases {

        @Test
        @DisplayName("odrzuca przelew przy niewystarczających środkach")
        void rejectsInsufficientFunds() {
            when(accountRepository.findById(10L)).thenReturn(Optional.of(fromAccount));
            when(accountRepository.findById(20L)).thenReturn(Optional.of(toAccount));

            assertThatThrownBy(() -> accountService.transferMoney(
                    10L, 20L, new BigDecimal("100.01"), "Za dużo"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Niewystarczające środki");

            assertThat(fromAccount.getBalance()).isEqualByComparingTo("100.00");
            assertThat(toAccount.getBalance()).isEqualByComparingTo("50.00");
            verify(transactionRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("gdy konto źródłowe nie istnieje — RuntimeException z komunikatem")
        void sourceAccountMissing() {
            when(accountRepository.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.transferMoney(10L, 20L, new BigDecimal("10.00"), "x"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Nie znaleziono konta o ID: 10");

            verify(transactionRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("gdy konto docelowe nie istnieje — RuntimeException z komunikatem")
        void targetAccountMissing() {
            when(accountRepository.findById(10L)).thenReturn(Optional.of(fromAccount));
            when(accountRepository.findById(20L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.transferMoney(10L, 20L, new BigDecimal("10.00"), "x"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Nie znaleziono konta o ID: 20");

            verify(transactionRepository, times(0)).save(any());
        }
    }
}
