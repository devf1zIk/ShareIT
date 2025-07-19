package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.CommentCreateDto;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
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
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(Long userId, ItemCreateDto itemDto) {
        User owner = getUserOrThrow(userId);
        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemUpdateDto itemDto) {
        User user = getUserOrThrow(userId);

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }

        Item item = optionalItem.get();

        if (!item.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenException("Вы не владелец этой вещи");
        }

        itemMapper.updateItemFromDto(itemDto, item);
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toDto(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItem(Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }

        Item item = optionalItem.get();
        ItemDto itemDto = itemMapper.toDto(item);

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);
        List<CommentDto> commentDtos = new ArrayList<>();

        for (Comment comment : comments) {
            CommentDto commentDto = commentMapper.toDto(comment);
            commentDtos.add(commentDto);
        }

        itemDto.setComments(commentDtos);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getUserItems(Long userId) {
        getUserOrThrow(userId);

        List<Item> items = itemRepository.findByOwnerId(userId);
        List<ItemDto> result = new ArrayList<>();

        for (Item item : items) {
            ItemDto dto = itemMapper.toDto(item);
            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> foundItems = itemRepository.searchAvailableItems(text);
        List<ItemDto> result = new ArrayList<>();

        for (Item item : foundItems) {
            result.add(itemMapper.toDto(item));
        }

        return result;
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentDto) {
        User user = getUserOrThrow(userId);

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }

        Item item = optionalItem.get();

        List<Booking> bookings = bookingRepository.findPastBookingsByUserAndItem(userId, itemId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new ValidationException("Вы не можете оставить отзыв на вещь, которую не арендовали.");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    private User getUserOrThrow(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }
        return optionalUser.get();
    }
}