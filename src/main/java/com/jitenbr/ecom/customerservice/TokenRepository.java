package com.jitenbr.ecom.customerservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Transactional
    @Modifying
    @Query("update Token t set t.status = ?1 where t.tokenid = ?2")
    int updateStatusByTokenid(String status, Integer tokenid);
}