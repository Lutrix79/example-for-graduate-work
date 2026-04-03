package ru.skypro.homework.repository;

import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.AdvertisementEntity;
import ru.skypro.homework.entity.UserEntity;

import java.util.List;
import java.util.Optional;
@Repository
public interface AdvertisementRepository {
    Optional<AdvertisementEntity> findById(Long id);
    List<AdvertisementEntity> findAll();
    AdvertisementEntity save(AdvertisementEntity entity);
    AdvertisementEntity delete(AdvertisementEntity entity);
    List<AdvertisementEntity> findAllByAuthor(UserEntity author);
}
