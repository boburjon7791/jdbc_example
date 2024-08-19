package com.example.jdbc_example.model.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class PaginationData {
    private int page;
    private int numberOfElements;
    private int totalPages;
    private long totalElements;
    private PaginationData(){}
    public static PaginationData of(Page<?> pagination){
        return new PaginationData(
                pagination.getNumber(),
                pagination.getNumberOfElements(),
                pagination.getTotalPages(),
                pagination.getTotalElements()
        );
    }
}
