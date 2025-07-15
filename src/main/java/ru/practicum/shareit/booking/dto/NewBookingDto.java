package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NewBookingDto {

    @NotNull(message = "Item ID must not be null")
    private Long itemId;

    @NotNull(message = "Start date must not be null")
    @Future(message = "Start date must be in the future")
    private LocalDateTime start;

    @NotNull(message = "End date must not be null")
    @Future(message = "End date must be in the future")
    private LocalDateTime end;
}
