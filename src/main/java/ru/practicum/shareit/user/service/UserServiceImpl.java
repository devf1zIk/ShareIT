package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserCreateDto userDto) {
        String email = userDto.getEmail();

        if (email != null) {
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                throw new ConflictException("Email уже используется: " + email);
            }
        }

        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateDto dto) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден: " + id);
        }

        User user = optionalUser.get();

        String newEmail = dto.getEmail();
        if (newEmail != null && !newEmail.equalsIgnoreCase(user.getEmail())) {
            Optional<User> userWithSameEmail = userRepository.findByEmail(newEmail);
            if (userWithSameEmail.isPresent()) {
                User foundUser = userWithSameEmail.get();
                if (!foundUser.getId().equals(id)) {
                    throw new ConflictException("Email уже используется: " + newEmail);
                }
            }
        }

        userMapper.updateUserFromDto(dto, user);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        boolean exists = userRepository.existsById(id);
        if (!exists) {
            throw new NotFoundException("Пользователь не найден: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден: " + id);
        }

        return userMapper.toDto(optionalUser.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> result = new ArrayList<>();

        for (User user : users) {
            UserDto dto = userMapper.toDto(user);
            result.add(dto);
        }

        return result;
    }
}