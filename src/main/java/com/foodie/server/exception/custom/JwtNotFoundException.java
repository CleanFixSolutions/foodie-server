package com.foodie.server.exception.custom;

import lombok.Getter;

@Getter
public class JwtNotFoundException extends CustomClientException {

    public JwtNotFoundException() {
        super("the header is missing or the JWT is incorrect.");
    }
}
