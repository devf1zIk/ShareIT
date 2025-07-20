package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest toEntity(NewItemRequestDto dto, User requester) {
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    public static ItemRequestDto toDto(ItemRequest request, List<ItemShortDto> items) {

        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription(request.getDescription());
        dto.setId(request.getId());
        dto.setCreated(request.getCreated());
        dto.setItems(items);
        return dto;
    }
}


