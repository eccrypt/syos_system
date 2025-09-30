package com.syos.repository;

import com.syos.model.Bill;

public interface BillingRepository {

    void save(Bill bill);

    int nextSerial();
}
