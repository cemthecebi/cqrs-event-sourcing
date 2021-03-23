package com.cemthecebi.cqrsevent.repository;

import com.cemthecebi.cqrsevent.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
}
