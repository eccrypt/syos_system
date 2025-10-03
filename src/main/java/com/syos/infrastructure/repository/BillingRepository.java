package com.syos.infrastructure.repository;

import java.util.List;

import com.syos.domain.model.Bill;

public interface BillingRepository {

    void save(Bill bill);

    int nextSerial();

    List<Bill> findAll();

    Bill findBySerial(int serialNumber);
}
