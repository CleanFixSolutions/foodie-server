package com.foodie.server.exception.custom;

import lombok.Getter;

@Getter
public class EmptyUserUpdateException extends CustomClientException {

    public EmptyUserUpdateException(String message) {
        super(message);
    }
}
