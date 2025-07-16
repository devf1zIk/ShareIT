package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.map.BaseEntity;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
}