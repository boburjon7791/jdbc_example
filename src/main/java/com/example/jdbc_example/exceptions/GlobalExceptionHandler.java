package com.example.jdbc_example.exceptions;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@RestControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(Exception.class)
    public HttpEntity<String> handler(Exception e){
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
    public static String getMessage(Exception e){
        String message = e.getMessage();
        int beginIndex = message.indexOf("+") + 1;
        int endIndex = message.indexOf("*");
        return beginIndex==0 || endIndex==-1 ? e.getMessage() : message.substring(beginIndex, endIndex);
    }
}
