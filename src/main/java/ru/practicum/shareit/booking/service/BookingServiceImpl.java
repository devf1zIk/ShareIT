package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final Map<Long, Booking> bookingStorage = new HashMap<>();
    private final AtomicLong bookingIdGen = new AtomicLong(1);

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
        validateBooking(bookingDto);

        Long id = bookingIdGen.getAndIncrement();
        Booking booking = BookingMapper.fromDto(bookingDto);
        booking.setId(id);
        booking.setStatus(BookingStatus.WAITING);
        bookingStorage.put(id, booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto approveBooking(Long bookingId, boolean approved) {
        Booking booking = bookingStorage.get(bookingId);
        if (booking == null) {
            throw new NotFoundException("Бронирование с id " + bookingId + " не найдено");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingStatusException("Бронирование уже подтверждено или отклонено");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getBooking(Long bookingId) {
        Booking booking = bookingStorage.get(bookingId);
        if (booking == null) {
            throw new NotFoundException("Бронирование с id " + bookingId + " не найдено");
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByBooker(Long userId) {
        return bookingStorage.values().stream()
                .filter(b -> Objects.equals(b.getBookerId(), userId))
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long ownerId) {
        return bookingStorage.values().stream()
                .filter(b -> Objects.equals(b.getBookerId(), ownerId))
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateBooking(BookingDto dto) {
        if (dto.getItemId() == null || dto.getBookerId() == null) {
            throw new ValidationException("ID вещи и ID арендатора обязательны");
        }

        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new ValidationException("Дата начала и окончания обязательны");
        }

        if (dto.getStart().isAfter(dto.getEnd())) {
            throw new ValidationException("Дата начала позже даты окончания");
        }

        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Нельзя бронировать в прошлом");
        }
    }
}
