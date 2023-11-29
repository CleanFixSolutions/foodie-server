package com.foodie.server.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomClientException extends RuntimeException {

    private final HttpStatus status;

    public CustomClientException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }
}
