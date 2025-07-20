package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createRequest(Long userId, NewItemRequestDto dto) {
        User user = getUserOrThrow(userId);

        ItemRequest request = ItemRequestMapper.toEntity(dto, user);

        ItemRequest savedRequest = itemRequestRepository.save(request);
        return ItemRequestMapper.toDto(savedRequest, new ArrayList<>());
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {
        getUserOrThrow(userId);

        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        List<ItemRequestDto> result = new ArrayList<>();

        for (ItemRequest request : requests) {
            List<Item> items = itemRepository.findByRequest(request.getId());

            List<ItemShortDto> itemDtos = new ArrayList<>();
            for (Item item : items) {
                itemDtos.add(itemMapper.toShortDto(item));
            }

            ItemRequestDto dto = ItemRequestMapper.toDto(request, itemDtos);
            result.add(dto);
        }

        return result;
    }

    @Override
    public List<ItemRequestDto> getOtherUsersRequests(Long userId, int from, int size) {
        getUserOrThrow(userId);

        List<ItemRequest> sortedRequests = itemRequestRepository.findByRequesterIdNot(
                userId, Sort.by(Sort.Direction.DESC, "created")
        );

        List<ItemRequest> pagedRequests = new ArrayList<>();
        int end = Math.min(from + size, sortedRequests.size());

        if (from < sortedRequests.size()) {
            pagedRequests = sortedRequests.subList(from, end);
        }

        List<ItemRequestDto> result = new ArrayList<>();

        for (ItemRequest request : pagedRequests) {
            List<Item> items = itemRepository.findByRequest(request.getId());

            List<ItemShortDto> itemDtos = new ArrayList<>();
            for (Item item : items) {
                itemDtos.add(itemMapper.toShortDto(item));
            }

            ItemRequestDto dto = ItemRequestMapper.toDto(request, itemDtos);
            result.add(dto);
        }

        return result;
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        getUserOrThrow(userId);

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));

        List<Item> items = itemRepository.findByRequest(requestId);
        List<ItemShortDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            itemDtos.add(itemMapper.toShortDto(item));
        }

        return ItemRequestMapper.toDto(request, itemDtos);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
