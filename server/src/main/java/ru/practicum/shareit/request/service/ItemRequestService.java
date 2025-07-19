package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(Long userId, NewItemRequestDto dto);

    List<ItemRequestDto> getOwnRequests(Long userId);

    List<ItemRequestDto> getOtherUsersRequests(Long userId, int from, int size);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}

