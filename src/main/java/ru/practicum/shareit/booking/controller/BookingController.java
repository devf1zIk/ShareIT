package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestBody BookingDto bookingDto) {
        BookingDto created = bookingService.createBooking(bookingDto);
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        BookingDto result = bookingService.approveBooking(bookingId, approved);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBooking(bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookingsByUser(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long ownerId
    ) {
        if (userId != null) {
            return ResponseEntity.ok(bookingService.getBookingsByBooker(userId));
        } else if (ownerId != null) {
            return ResponseEntity.ok(bookingService.getBookingsByOwner(ownerId));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}