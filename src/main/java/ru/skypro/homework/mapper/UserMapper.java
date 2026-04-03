package ru.skypro.homework.mapper;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;

@Component
public class UserMapper {

    public User toDto(UserEntity entity) {
        if (entity == null) return null;

        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .phone(entity.getPhone())
                .role(entity.getRole().name())
                .image(entity.getImage())
                .build();
    }

}
