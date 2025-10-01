package com.example.identity_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T>{
    private Integer code = 1000;
    private String message;
    private T content;
    private Integer page;
    private Integer size;
    private Integer totalElements;
    private Integer totalPages;
}
