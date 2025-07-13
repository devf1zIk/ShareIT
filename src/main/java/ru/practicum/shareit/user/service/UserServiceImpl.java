package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new HashMap<>();
    private long idGen = 1;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        checkEmailUniqueness(userDto.getEmail(), null);
        User user = UserMapper.fromDto(userDto);
        user.setId(idGen++);
        users.put(user.getId(), user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User existing = users.get(id);
        if (existing == null) throw new NotFoundException("Пользователь не найден: " + id);

        if (userDto.getEmail() != null) {
            checkEmailUniqueness(userDto.getEmail(), id);
            existing.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }

        return UserMapper.toDto(existing);
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден: " + id);
        }
        users.remove(id);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = users.get(id);
        if (user == null) throw new NotFoundException("Пользователь не найден: " + id);
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    private void checkEmailUniqueness(String email, Long excludeId) {
        boolean exists = users.values().stream()
                .anyMatch(u -> !Objects.equals(u.getId(), excludeId) && u.getEmail().equalsIgnoreCase(email));
        if (exists) {
            throw new ConflictException("Email уже используется: " + email);
        }
    }

}
