package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import java.util.List;

public interface UserService {

    UserDto createUser(UserCreateDto userDto);

    UserDto updateUser(Long id, UserUpdateDto userDto);

    void deleteUser(Long id);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();
}
