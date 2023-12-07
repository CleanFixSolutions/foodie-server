package com.foodie.server.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponseDto {

    @JsonProperty("recipe_id")
    private Long id;

    @NotEmpty
    @JsonProperty("author")
    private String author;

    @JsonProperty("recipe_blocks")
    private List<@Valid RecipeBlockDto> recipeBlocks;

    @JsonProperty("creation_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date creationTime;

    @JsonProperty("last_modification_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date lastModificationTime;
}
