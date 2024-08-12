package com.example.jdbc_example.model.base;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
//@Builder
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
public class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
//    @Builder.Default
    protected LocalDateTime createdAt=LocalDateTime.now();
//    @Builder.Default
    protected Boolean deleted=false;
    public static final String _id="id";
    public static final String _createdAt="created_at";
    public static final String _deleted="deleted";
}
