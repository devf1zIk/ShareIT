package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название не должно быть пустым")
    private String name;

    @NotBlank(message = "Описание не должно быть пустым")
    private String description;

    @NotNull(message = "Поле available обязательно")
    private Boolean available;
}
