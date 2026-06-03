package com.erp.system.auth.controller;

import com.erp.system.auth.entity.Party;
import com.erp.system.auth.service.PartyService;
import com.erp.system.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parties")
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;

    @PostMapping
    public ApiResponse<Party> createParty(@RequestBody Party party) {
        return ApiResponse.success(partyService.createParty(party), "Party created successfully");
    }

    @GetMapping("/all")
    public ApiResponse<List<Party>> getAllParties() {
        return ApiResponse.success(partyService.getAllParties());
    }

    @GetMapping("/{id}")
    public ApiResponse<Party> getParty(@PathVariable Long id) {
        try {
            return ApiResponse.success(partyService.getParty(id));
        } catch (RuntimeException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Party> updateParty(@PathVariable Long id, @RequestBody Party party) {
        try {
            return ApiResponse.success(partyService.updateParty(id, party), "Party updated successfully");
        } catch (RuntimeException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteParty(@PathVariable Long id) {
        try {
            partyService.deleteParty(id);
            return ApiResponse.success(null, "Party deleted successfully");
        } catch (RuntimeException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }
}