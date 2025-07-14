package ru.practicum.shareit.booking.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    Booking toBooking(NewBookingDto newBookingDto, @Context User booker, @Context Item item);

    @AfterMapping
    default void setContext(@MappingTarget Booking booking,
                            @Context User booker,
                            @Context Item item) {
        booking.setBooker(booker);
        booking.setItem(item);
    }
}
