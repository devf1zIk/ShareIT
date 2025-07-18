package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long userId, NewBookingDto newBookingDto);

    BookingDto approveBooking(Long userId, Long bookingId, boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllBookingsByUser(Long userId, String state);

    List<BookingDto> getAllBookingsForOwner(Long ownerId, String state);
}
