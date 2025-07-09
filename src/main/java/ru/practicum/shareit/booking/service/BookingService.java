package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto);

    BookingDto approveBooking(Long bookingId, boolean approved);

    BookingDto getBooking(Long bookingId);

    List<BookingDto> getBookingsByBooker(Long userId);

    List<BookingDto> getBookingsByOwner(Long ownerId);
}
