package com.cemthecebi.cqrsevent.event;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class AccountCreatedEvent {

    private final UUID id;
    private final BigDecimal initialBalance;
    private final String owner;
}
