package com.example.jdbc_example.model.base;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "employees",
        uniqueConstraints = {
            @UniqueConstraint(name = "e_unique_login", columnNames = {Employee._username})
        }
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee extends BaseModel {
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "username", nullable = false)
    private String username;
    @Builder.Default
    private Boolean active=true;
    public static final String _firstName="first_name";
    public static final String _lastName="last_name";
    public static final String _username="username";
    public static final String _active="active";
}
