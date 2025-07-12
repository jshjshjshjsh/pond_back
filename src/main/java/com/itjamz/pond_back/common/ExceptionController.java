package com.itjamz.pond_back.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException e) {
        //return createErrorResponse(e);
        return createErrorResponse(e, org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        return createErrorResponse(e, org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException e) {
        return createErrorResponse(e, org.springframework.http.HttpStatus.CONFLICT);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(Exception e, org.springframework.http.HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("message", e.getMessage());

        String location = "";
        if (e.getStackTrace().length > 0) {
            StackTraceElement topElement = e.getStackTrace()[0];
            location = String.format("at %s.%s(line:%d)", topElement.getClassName(), topElement.getMethodName(), topElement.getLineNumber());
        }
        log.warn("[error] {} {}", e.getMessage(), location);

        return new ResponseEntity<>(errorResponse, status);
    }
}
