package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", uses = {ru.practicum.shareit.item.mapper.ItemMapper.class, ru.practicum.shareit.user.mapper.UserMapper.class})
public interface BookingMapper {

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