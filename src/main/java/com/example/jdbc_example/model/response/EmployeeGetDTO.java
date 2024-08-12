package com.example.jdbc_example.model.response;

import com.example.jdbc_example.model.base.Employee;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EmployeeGetDTO(
        Long id,
        LocalDateTime createdAt,
        String firstName,
        String lastName,
        String username
) {
    public static EmployeeGetDTO of(
            Long id,
            LocalDateTime createdAt,
            String firstName,
            String lastName,
            String username
    ){
        return new EmployeeGetDTO(
                id,
                createdAt,
                firstName,
                lastName,
                username
        );
    }

    public static EmployeeGetDTO fromEntity(Employee employee) {
        return EmployeeGetDTO.builder()
                .id(employee.getId())
                .createdAt(employee.getCreatedAt())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .username(employee.getUsername())
                .build();
    }
}
