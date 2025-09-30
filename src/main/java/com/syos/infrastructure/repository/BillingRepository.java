package com.syos.infrastructure.repository;

import com.syos.domain.model.Bill;

public interface BillingRepository {

    void save(Bill bill);

    int nextSerial();
}
