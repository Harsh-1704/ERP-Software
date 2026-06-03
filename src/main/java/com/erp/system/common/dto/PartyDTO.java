package com.erp.system.common.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PartyDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePartyRequest {
        @NotBlank(message = "Party name is required")
        private String name;

        @NotBlank(message = "Party type is required")
        private String partyType;

        private String email;
        private String phone;
        private String mobile;
        private String website;

        private String gstNumber;
        private String panNumber;
        private String cinNumber;

        private String billingAddress;
        private String shippingAddress;

        private String bankName;
        private String bankAccountNumber;
        private String bankIfscCode;
        private String bankBranchName;

        private String remarks;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyResponse {
        private Long id;
        private String name;
        private String partyType;
        private String email;
        private String phone;
        private String mobile;
        private String website;
        private String gstNumber;
        private String panNumber;
        private String cinNumber;
        private String billingAddress;
        private String shippingAddress;
        private String bankName;
        private String bankAccountNumber;
        private String bankIfscCode;
        private String bankBranchName;
        private String remarks;
        private Boolean isActive;
        private java.util.List<AddressInfo> addresses;
        private java.util.List<ContactInfo> contacts;
        private java.util.List<RoleInfo> roles;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AddressInfo {
            private Long id;
            private String addressLine1;
            private String addressLine2;
            private String city;
            private String state;
            private String country;
            private String pincode;
            private boolean isDefault;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ContactInfo {
            private Long id;
            private String name;
            private String email;
            private String phone;
            private String mobile;
            private String designation;
            private boolean isPrimary;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RoleInfo {
            private Long id;
            private String name;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddAddressRequest {
        @NotBlank(message = "Address line 1 is required")
        private String addressLine1;

        private String addressLine2;

        @NotBlank(message = "City is required")
        private String city;

        private String state;

        private String country;

        private String pincode;

        private Boolean isDefault;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddContactRequest {
        @NotBlank(message = "Contact name is required")
        private String name;

        private String email;

        private String phone;

        private String mobile;

        private String designation;

        private Boolean isPrimary;
    }
}