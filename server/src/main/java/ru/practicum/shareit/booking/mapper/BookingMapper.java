package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "item", target = "item")
    @Mapping(source = "booker", target = "booker")
    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "WAITING")
    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "item", ignore = true)
    Booking toBooking(NewBookingDto dto, @Context User booker, @Context Item item);

    @AfterMapping
    default void setContext(@MappingTarget Booking booking,
                            @Context User booker,
                            @Context Item item) {
        booking.setBooker(booker);
        booking.setItem(item);
    }
}
