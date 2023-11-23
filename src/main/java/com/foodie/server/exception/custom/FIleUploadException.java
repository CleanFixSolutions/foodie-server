package com.foodie.server.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FIleUploadException extends RuntimeException {

    private final HttpStatus status;

    public FIleUploadException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
