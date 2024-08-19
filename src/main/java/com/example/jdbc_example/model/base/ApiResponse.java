package com.example.jdbc_example.model.base;

import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private T data;
    private PaginationData pagination;
    public static <T> ApiResponse<T> ok(T data, PaginationData pagination){
        return ApiResponse.<T>builder()
                .data(data)
                .pagination(pagination)
                .build();
    }
    public static <T> ApiResponse<T> ok(T data){
        return ApiResponse.<T>builder()
                .data(data)
                .build();
    }
}
