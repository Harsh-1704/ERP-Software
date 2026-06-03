package com.erp.system.marketplace.repository;

import com.erp.system.auth.entity.Party;
import com.erp.system.marketplace.entity.BulkOrder;
import com.erp.system.marketplace.entity.BulkOrderStatus;
import com.erp.system.marketplace.entity.MarketplaceListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BulkOrderRepository extends JpaRepository<BulkOrder, Long> {
    Optional<BulkOrder> findByOrderNumber(String orderNumber);
    List<BulkOrder> findByListing(MarketplaceListing listing);
    List<BulkOrder> findByBuyerParty(Party buyer);
    List<BulkOrder> findByStatus(BulkOrderStatus status);
}
