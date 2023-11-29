package com.foodie.server.exception.custom;

import lombok.Getter;

@Getter
public class FileUploadClientException extends CustomClientException {

    public FileUploadClientException(String message) {
        super(message);
    }
}
