package com.erp.system.common.mapper;

import com.erp.system.auth.entity.Party;
import com.erp.system.auth.entity.PartyRole;
import com.erp.system.auth.entity.Role;
import com.erp.system.auth.entity.User;
import com.erp.system.common.dto.PartyDTO;
import com.erp.system.common.dto.UserDTO;
import com.erp.system.inventory.entity.Warehouse;
import com.erp.system.common.dto.InventoryDTO;
import com.erp.system.product.entity.Product;
import com.erp.system.product.entity.ProductCategory;
import com.erp.system.product.entity.Unit;
import com.erp.system.common.dto.ProductDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    // User mappers
    public UserDTO.UserResponse toUserResponse(User user) {
        UserDTO.UserResponse response = new UserDTO.UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(null);
        response.setActive(user.isActive());
        if (user.getRole() != null) {
            response.setRole(new UserDTO.UserResponse.RoleInfo(
                    user.getRole().getId(),
                    user.getRole().getName()
            ));
        }
        return response;
    }

    public List<UserDTO.UserResponse> toUserResponseList(List<User> users) {
        return users.stream().map(this::toUserResponse).collect(Collectors.toList());
    }

    // Party mappers
    public PartyDTO.PartyResponse toPartyResponse(Party party) {
        PartyDTO.PartyResponse response = new PartyDTO.PartyResponse();
        response.setId(party.getId());
        response.setName(party.getPartyName());
        response.setPartyType(party.getPartyRoles().stream()
                .findFirst()
                .map(PartyRole::getPartyType)
                .map(pt -> pt.getTypeName())
                .orElse(null));

        Optional<com.erp.system.auth.entity.Contact> primaryContact = party.getContacts().stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsPrimary()))
                .findFirst();
        com.erp.system.auth.entity.Contact fallbackContact = party.getContacts().stream().findFirst().orElse(null);
        com.erp.system.auth.entity.Contact contact = primaryContact.orElse(fallbackContact);

        response.setEmail(contact != null ? contact.getEmail() : null);
        response.setPhone(contact != null ? contact.getPhone() : null);
        response.setMobile(contact != null ? contact.getMobile() : null);
        response.setWebsite(null);
        response.setGstNumber(party.getGstNumber());
        response.setPanNumber(party.getPanNumber());
        response.setCinNumber(null);
        response.setBillingAddress(party.getAddresses().stream()
                .filter(a -> "BILLING".equalsIgnoreCase(a.getAddressType()))
                .findFirst()
                .map(a -> a.getStreet())
                .orElse(null));
        response.setShippingAddress(party.getAddresses().stream()
                .filter(a -> "SHIPPING".equalsIgnoreCase(a.getAddressType()))
                .findFirst()
                .map(a -> a.getStreet())
                .orElse(null));
        response.setBankName(null);
        response.setBankAccountNumber(null);
        response.setBankIfscCode(null);
        response.setBankBranchName(null);
        response.setRemarks(null);
        response.setIsActive(!"INACTIVE".equalsIgnoreCase(String.valueOf(party.getStatus())));
        return response;
    }

    public List<PartyDTO.PartyResponse> toPartyResponseList(List<Party> parties) {
        return parties.stream().map(this::toPartyResponse).collect(Collectors.toList());
    }

    // Product mappers
    public ProductDTO.ProductResponse toProductResponse(Product product) {
        ProductDTO.ProductResponse response = new ProductDTO.ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setSku(product.getSku());
        response.setBarcode(product.getBarcode());
        response.setPrice(product.getPrices().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsCurrent()))
                .map(p -> p.getPrice())
                .findFirst()
                .orElse(null));
        response.setCostPrice(product.getCostPrice());
        response.setMinStockLevel(product.getMinStockLevel());
        response.setMaxStockLevel(product.getMaxStockLevel());
        response.setTaxRate(product.getTaxRate());
        response.setManufacturer(product.getManufacturer());
        response.setHsnCode(product.getHsnCode());
        response.setActive(Boolean.TRUE.equals(product.getActive()));
        if (product.getCategory() != null) {
            response.setCategory(new ProductDTO.ProductResponse.CategoryInfo(
                    product.getCategory().getId(),
                    product.getCategory().getName()
            ));
        }
        if (product.getUnit() != null) {
            response.setUnit(new ProductDTO.ProductResponse.UnitInfo(
                    product.getUnit().getId(),
                    product.getUnit().getName(),
                    product.getUnit().getSymbol()
            ));
        }
        return response;
    }

    public List<ProductDTO.ProductResponse> toProductResponseList(List<Product> products) {
        return products.stream().map(this::toProductResponse).collect(Collectors.toList());
    }

    public ProductDTO.CategoryResponse toCategoryResponse(ProductCategory category) {
        ProductDTO.CategoryResponse response = new ProductDTO.CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        if (category.getParent() != null) {
            response.setParent(toCategoryResponse(category.getParent()));
        }
        return response;
    }

    public ProductDTO.UnitResponse toUnitResponse(Unit unit) {
        return new ProductDTO.UnitResponse(unit.getId(), unit.getName(), unit.getSymbol());
    }

    // Warehouse mappers
    public InventoryDTO.WarehouseResponse toWarehouseResponse(Warehouse warehouse) {
        InventoryDTO.WarehouseResponse response = new InventoryDTO.WarehouseResponse();
        response.setId(warehouse.getId());
        response.setName(warehouse.getName());
        response.setCode(warehouse.getCode());
        response.setAddressLine1(warehouse.getAddressLine1());
        response.setCity(warehouse.getCity());
        response.setState(warehouse.getState());
        response.setCountry(warehouse.getCountry());
        response.setContactPerson(warehouse.getContactPerson());
        response.setContactEmail(warehouse.getContactEmail());
        response.setContactPhone(warehouse.getContactPhone());
        response.setActive(Boolean.TRUE.equals(warehouse.getIsActive()));
        response.setCapacityUnits(warehouse.getCapacityUnits());
        return response;
    }

    public List<InventoryDTO.WarehouseResponse> toWarehouseResponseList(List<Warehouse> warehouses) {
        return warehouses.stream().map(this::toWarehouseResponse).collect(Collectors.toList());
    }

    // Role mapper
    public Role toRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return role;
    }
}