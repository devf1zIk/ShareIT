package ru.practicum.shareit.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

@Mapper(componentModel = "spring")
@ComponentScan("ru.practicum.shareit")
public interface CommentMapper {

    @Mapping(source = "author.name", target = "authorName")
    CommentDto toDto(Comment comment);
}
