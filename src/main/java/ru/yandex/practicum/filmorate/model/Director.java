package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Builder
@EqualsAndHashCode(of = "id")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Director {

    private long id;

    @NotBlank(message = "Name cannot be empty or null")
    String name;


    public void setId(long id) {
        this.id = id;
    }
}
