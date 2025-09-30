package com.syos.application.factory;

import com.syos.application.dto.CustomerRegisterRequestDTO;
import com.syos.domain.enums.UserType;
import com.syos.domain.model.Customer;
import com.syos.domain.model.User;

public class UserFactory {

    public static User createUser(CustomerRegisterRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new NullPointerException("CustomerRegisterRequestDTO cannot be null");
        }

        UserType userType = requestDTO.getUserType();
        if (userType == null) {
            throw new IllegalArgumentException("User type cannot be null");
        }

        switch (userType) {
            case CUSTOMER:
                return new Customer.CustomerBuilder()
                        .firstName(requestDTO.getFirstName())
                        .lastName(requestDTO.getLastName())
                        .email(requestDTO.getEmail())
                        .password(requestDTO.getPassword())
                        .build();
            default:
                throw new IllegalArgumentException("Unsupported user type: " + userType);
        }
    }
}