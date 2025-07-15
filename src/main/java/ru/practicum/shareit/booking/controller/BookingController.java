package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody NewBookingDto newBookingDto,@RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(userId, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId,
                              @RequestParam boolean approved,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsForOwner(ownerId, state);
    }
}