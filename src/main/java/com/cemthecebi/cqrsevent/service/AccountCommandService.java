package com.cemthecebi.cqrsevent.service;

import com.cemthecebi.cqrsevent.command.CreateAccountCommand;
import com.cemthecebi.cqrsevent.command.CreditMoneyCommand;
import com.cemthecebi.cqrsevent.command.DebitMoneyCommand;
import com.cemthecebi.cqrsevent.dto.AccountCreationDTO;
import com.cemthecebi.cqrsevent.dto.MoneyAmountDTO;
import com.cemthecebi.cqrsevent.entity.BankAccount;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.cemthecebi.cqrsevent.utils.ServiceUtils.formatUuid;

@Service
@AllArgsConstructor
public class AccountCommandService {
    private final CommandGateway commandGateway;

    public CompletableFuture<BankAccount> createAccount(AccountCreationDTO creationDTO) {
        return this.commandGateway.send(new CreateAccountCommand(
                UUID.randomUUID(),
                creationDTO.getInitialBalance(),
                creationDTO.getOwner()
        ));
    }

    public CompletableFuture<String> creditMoneyToAccount(String accountId,
                                                          MoneyAmountDTO moneyCreditDTO) {
        return this.commandGateway.send(new CreditMoneyCommand(
                formatUuid(accountId),
                moneyCreditDTO.getAmount()
        ));
    }

    public CompletableFuture<String> debitMoneyFromAccount(String accountId,
                                                           MoneyAmountDTO moneyDebitDTO) {
        return this.commandGateway.send(new DebitMoneyCommand(
                formatUuid(accountId),
                moneyDebitDTO.getAmount()
        ));
    }
}