package ru.practicum.shareit.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentCreateDto {

    @NotBlank(message = "Комментарий не должен быть пустым")
    private String text;
}