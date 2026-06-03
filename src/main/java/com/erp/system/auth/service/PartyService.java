package com.erp.system.auth.service;

import com.erp.system.auth.entity.Party;
import com.erp.system.auth.repository.PartyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final PartyRepository partyRepository;

    public Party createParty(Party party) {
        return partyRepository.save(party);
    }

    public List<Party> getAllParties() {
        return partyRepository.findAll();
    }

    public Party getParty(Long id) {
        return partyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Party not found"));
    }

    public Party updateParty(Long id, Party updates) {
        Party existing = getParty(id);
        if (updates.getPartyName() != null) {
            existing.setPartyName(updates.getPartyName());
        }
        if (updates.getLegalName() != null) {
            existing.setLegalName(updates.getLegalName());
        }
        if (updates.getGstNumber() != null) {
            existing.setGstNumber(updates.getGstNumber());
        }
        if (updates.getPanNumber() != null) {
            existing.setPanNumber(updates.getPanNumber());
        }
        if (updates.getStatus() != null) {
            existing.setStatus(updates.getStatus());
        }
        return partyRepository.save(existing);
    }

    public void deleteParty(Long id) {
        Party existing = getParty(id);
        partyRepository.delete(existing);
    }
}