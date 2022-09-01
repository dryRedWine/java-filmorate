package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Review {
    int reviewId;
    @NotNull
    @NotEmpty
    String content;
    @NotNull
    @JsonProperty("isPositive")
    Boolean isPositive;
    @NotNull
    Integer userId;
    @NotNull
    Integer filmId;
    int useful;

}
