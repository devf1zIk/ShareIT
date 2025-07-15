package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "booker.id", target = "bookerId")
    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "WAITING")
    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "item", ignore = true)
    Booking toBooking(NewBookingDto newBookingDto, @Context Optional<User> booker, @Context Item item);

    @AfterMapping
    default void setContext(@MappingTarget Booking booking,
                            @Context User booker,
                            @Context Item item) {
        booking.setBooker(booker);
        booking.setItem(item);
    }
}
