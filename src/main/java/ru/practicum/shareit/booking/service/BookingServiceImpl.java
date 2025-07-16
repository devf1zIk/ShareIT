package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(Long userId, NewBookingDto newBookingDto) {
        validateUserExists(userId);

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }

        Optional<Item> optionalItem = itemRepository.findById(newBookingDto.getItemId());
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }

        User booker = optionalUser.get();
        Item item = optionalItem.get();

        Booking booking = bookingMapper.toBooking(newBookingDto, booker, item);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(saved);
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, boolean approved) {
        validateUserExists(userId);

        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new ValidationException("Бронирование не найдено");
        }

        Booking booking = optionalBooking.get();

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Только владелец может подтвердить бронирование");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingStatusException("Бронирование уже подтверждено или отклонено");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long userId, Long bookingId) {
        validateUserExists(userId);

        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new NotFoundException("Бронирование не найдено");
        }

        Booking booking = optionalBooking.get();

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
            throw new ForbiddenException("У вас нет доступа к этому бронированию");
        }

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingsByUser(Long userId, String stateParam) {
        validateUserExists(userId);

        BookingState state = BookingState.from(stateParam);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> bookings = new ArrayList<>();

        if (state == BookingState.CURRENT) {
            bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, now, now, sort);
        } else if (state == BookingState.PAST) {
            bookings = bookingRepository.findByBookerIdAndEndBefore(userId, now, sort);
        } else if (state == BookingState.FUTURE) {
            bookings = bookingRepository.findByBookerIdAndStartAfter(userId, now, sort);
        } else if (state == BookingState.WAITING) {
            bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
        } else if (state == BookingState.REJECTED) {
            bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
        } else {
            bookings = bookingRepository.findByBookerId(userId, sort);
        }

        List<BookingDto> result = new ArrayList<>();
        for (Booking booking : bookings) {
            result.add(bookingMapper.toBookingDto(booking));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingsForOwner(Long ownerId, String stateParam) {
        validateUserExists(ownerId);

        BookingState state = BookingState.from(stateParam);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> bookings = new ArrayList<>();

        if (state == BookingState.CURRENT) {
            bookings = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(ownerId, now, now, sort);
        } else if (state == BookingState.PAST) {
            bookings = bookingRepository.findByItem_Owner_IdAndEndBefore(ownerId, now, sort);
        } else if (state == BookingState.FUTURE) {
            bookings = bookingRepository.findByItem_Owner_IdAndStartAfter(ownerId, now, sort);
        } else if (state == BookingState.WAITING) {
            bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING, sort);
        } else if (state == BookingState.REJECTED) {
            bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED, sort);
        } else {
            bookings = bookingRepository.findByItem_Owner_Id(ownerId, sort);
        }

        List<BookingDto> result = new ArrayList<>();
        for (Booking booking : bookings) {
            result.add(bookingMapper.toBookingDto(booking));
        }

        return result;
    }

    private void validateUserExists(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }
    }
}