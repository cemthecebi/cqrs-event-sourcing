package com.cemthecebi.cqrsevent.aggregate;


import com.cemthecebi.cqrsevent.command.CreateAccountCommand;
import com.cemthecebi.cqrsevent.command.CreditMoneyCommand;
import com.cemthecebi.cqrsevent.command.DebitMoneyCommand;
import com.cemthecebi.cqrsevent.event.AccountCreatedEvent;
import com.cemthecebi.cqrsevent.event.MoneyCreditedEvent;
import com.cemthecebi.cqrsevent.event.MoneyDebitedEvent;
import com.cemthecebi.cqrsevent.exception.InsufficientBalanceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Aggregate
public class BankAccountAggregate {

    @AggregateIdentifier
    private UUID id;
    private BigDecimal balance;
    private String owner;

    @CommandHandler
    public BankAccountAggregate(CreateAccountCommand createAccountCommand) {
        AggregateLifecycle.apply(AccountCreatedEvent.builder()
                .id(createAccountCommand.getAccountId())
                .initialBalance(createAccountCommand.getInitialBalance())
                .owner(createAccountCommand.getOwner())
                .build());
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent accountCreatedEvent) {
        this.id = accountCreatedEvent.getId();
        this.owner = accountCreatedEvent.getOwner();
        this.balance = accountCreatedEvent.getInitialBalance();
    }

    @CommandHandler
    public void handle(CreditMoneyCommand creditMoneyCommand) {
        AggregateLifecycle.apply(MoneyCreditedEvent.builder()
                .id(creditMoneyCommand.getAccountId())
                .creditAmount(creditMoneyCommand.getCreditAmount())
                .build()
        );
    }

    @EventSourcingHandler
    public void on(MoneyCreditedEvent moneyCreditedEvent) {
        this.balance = this.balance.add(moneyCreditedEvent.getCreditAmount());
    }

    @CommandHandler
    public void handle(DebitMoneyCommand debitMoneyCommand) {
        AggregateLifecycle.apply(
                MoneyDebitedEvent.builder()
                        .id(debitMoneyCommand.getAccountId())
                        .debitAmount(debitMoneyCommand.getDebitAmount())
                        .build()
        );
    }

    @EventSourcingHandler
    public void on(MoneyDebitedEvent moneyDebitedEvent) throws InsufficientBalanceException {
        if (this.balance.compareTo(moneyDebitedEvent.getDebitAmount()) < 0) {
            throw new InsufficientBalanceException(moneyDebitedEvent.getId(), moneyDebitedEvent.getDebitAmount());
        }
        this.balance = this.balance.subtract(moneyDebitedEvent.getDebitAmount());
    }
}
