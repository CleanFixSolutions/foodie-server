package com.foodie.server.exception.custom;

import lombok.Getter;

@Getter
public class RecipeNotFoundClientException extends CustomClientException {

    public RecipeNotFoundClientException(String username, Long recipeId) {
        super("User with username='" + username + "' don't have recipe with id=" + recipeId);
    }

}
