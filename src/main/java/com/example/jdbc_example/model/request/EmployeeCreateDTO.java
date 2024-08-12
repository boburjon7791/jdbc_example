package com.example.jdbc_example.model.request;

import com.example.jdbc_example.model.base.Employee;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmployeeCreateDTO(
        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        String username
) {
        public Employee toEntity() {
                return Employee.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .username(username)
                        .build();
        }
}
