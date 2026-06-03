package com.erp.system.auth.repository;

import com.erp.system.auth.entity.PartyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyTypeRepository extends JpaRepository<PartyType, Long> {
}
