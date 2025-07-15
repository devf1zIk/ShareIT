package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.map.BaseEntity;
import ru.practicum.shareit.user.model.User;

@Entity
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Item extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Boolean available;

    @Column(nullable = false)
    private Long request;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}