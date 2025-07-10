package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new HashMap<>();
    private long idGen = 1;

    @Override
    public User createUser(User user) {
        validateUser(user);

        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new ConflictException("Email уже используется: " + user.getEmail());
        }

        user.setId(idGen++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Long id, User updated) {
        User existing = users.get(id);
        if (existing == null) {
            throw new NotFoundException("Пользователь не найден: " + id);
        }

        if (updated.getEmail() != null) {
            if (!updated.getEmail().contains("@")) {
                throw new BadRequestException("Некорректный email");
            }

            if (users.values().stream()
                    .anyMatch(u -> !u.getId().equals(id) && u.getEmail().equals(updated.getEmail()))) {
                throw new ConflictException("Email уже используется: " + updated.getEmail());
            }

            existing.setEmail(updated.getEmail());
        }

        if (updated.getName() != null) {
            existing.setName(updated.getName());
        }

        return existing;
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден: " + id);
        }
        users.remove(id);
    }

    @Override
    public User getUserById(Long id) {
        User user = users.get(id);
        if (user == null) throw new NotFoundException("Пользователь не найден: " + id);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BadRequestException("Email не должен быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            throw new BadRequestException("Некорректный email");
        }
    }
}
