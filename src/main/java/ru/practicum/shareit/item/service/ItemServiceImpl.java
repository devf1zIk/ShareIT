package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final Map<Long, Item> itemStorage = new HashMap<>();
    private long nextItemId = 1L;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        validateItemDto(itemDto);
        Long id = nextItemId++;
        Item item = ItemMapper.fromDto(itemDto, userId);
        item.setId(id);
        itemStorage.put(id, item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        Item existing = itemStorage.get(itemId);
        if (existing == null) {
            throw new NotFoundException("Вещь с id " + itemId + " не найдена.");
        }
        if (!existing.getOwnerId().equals(userId)) {
            throw new ForbiddenException("Вы не владелец этой вещи.");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existing.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existing.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existing.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toDto(existing);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemStorage.get(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь с id " + itemId + " не найдена.");
        }
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemStorage.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) return List.of();

        String lowerText = text.toLowerCase();
        return itemStorage.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()) &&
                        (item.getName().toLowerCase().contains(lowerText)
                                || item.getDescription().toLowerCase().contains(lowerText)))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateItemDto(ItemDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("Название вещи не может быть пустым.");
        }

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым.");
        }

        if (dto.getAvailable() == null) {
            throw new ValidationException("Поле доступности (available) обязательно.");
        }
    }
}

