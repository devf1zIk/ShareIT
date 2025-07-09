package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
        if (booking == null) return null;
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItemId())
                .bookerId(booking.getBookerId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public static Booking fromDto(BookingDto dto) {
        if (dto == null) return null;
        return Booking.builder()
                .id(dto.getId())
                .itemId(dto.getItemId())
                .bookerId(dto.getBookerId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .status(dto.getStatus())
                .build();
    }
}
