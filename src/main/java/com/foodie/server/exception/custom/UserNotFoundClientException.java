package com.foodie.server.exception.custom;

import lombok.Getter;

@Getter
public class UserNotFoundClientException extends CustomClientException {

    public UserNotFoundClientException(String username) {
        super("User with username '" + username + "' not found");
    }
}
