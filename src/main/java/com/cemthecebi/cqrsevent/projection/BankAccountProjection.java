package com.cemthecebi.cqrsevent.projection;

import com.cemthecebi.cqrsevent.entity.BankAccount;
import com.cemthecebi.cqrsevent.event.AccountCreatedEvent;
import com.cemthecebi.cqrsevent.event.MoneyCreditedEvent;
import com.cemthecebi.cqrsevent.event.MoneyDebitedEvent;
import com.cemthecebi.cqrsevent.exception.AccountNotFoundException;
import com.cemthecebi.cqrsevent.query.FindBankAccountQuery;
import com.cemthecebi.cqrsevent.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BankAccountProjection {

    private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountProjection.class);

    private final BankAccountRepository bankAccountRepository;

    @EventHandler
    public void on(AccountCreatedEvent accountCreatedEvent) {
        LOGGER.debug("Handling a Bank Account creation command {}", accountCreatedEvent.getId());
        BankAccount bankAccount = new BankAccount(
                accountCreatedEvent.getId(),
                accountCreatedEvent.getOwner(),
                accountCreatedEvent.getInitialBalance()
        );
        this.bankAccountRepository.save(bankAccount);
    }

    @EventHandler
    public void on(MoneyCreditedEvent event) throws AccountNotFoundException {
        LOGGER.debug("Handling an Account Credit command {}", event.getId());
        Optional<BankAccount> optionalBankAccount = this.bankAccountRepository.findById(event.getId());
        if (optionalBankAccount.isPresent()) {
            BankAccount bankAccount = optionalBankAccount.get();
            bankAccount.setBalance(bankAccount.getBalance().add(event.getCreditAmount()));
            this.bankAccountRepository.save(bankAccount);
        } else {
            throw new AccountNotFoundException(event.getId());
        }
    }

    @EventHandler
    public void on(MoneyDebitedEvent event) throws AccountNotFoundException {
        LOGGER.debug("Handling an Account Debit command {}", event.getId());
        Optional<BankAccount> optionalBankAccount = this.bankAccountRepository.findById(event.getId());
        if (optionalBankAccount.isPresent()) {
            BankAccount bankAccount = optionalBankAccount.get();
            bankAccount.setBalance(bankAccount.getBalance().subtract(event.getDebitAmount()));
            this.bankAccountRepository.save(bankAccount);
        } else {
            throw new AccountNotFoundException(event.getId());
        }
    }

    @QueryHandler
    public BankAccount handle(FindBankAccountQuery query) {
        LOGGER.debug("Handling FindBankAccountQuery query: {}", query);
        return this.bankAccountRepository.findById(query.getAccountId()).orElse(null);
    }
}
