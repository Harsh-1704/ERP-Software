package com.erp.system.auth.repository;

import com.erp.system.auth.entity.PartyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRoleRepository extends JpaRepository<PartyRole, Long> {
}