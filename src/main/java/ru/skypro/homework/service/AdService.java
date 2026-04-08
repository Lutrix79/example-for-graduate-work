package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;

import java.util.List;

public interface AdService {

    List<Ad> getAllAds();

    /**
     * Создает объявление для пользователя с указанным email
     */
    Ad createAd(CreateOrUpdateAd adDto, MultipartFile image, String email);

    Ad getAdById(Long id);

    /**
     * Удаляет объявление, если у пользователя с указанным email есть доступ
     */
    void deleteAd(Long id, String email);

    /**
     * Обновляет объявление для пользователя с указанным email
     */
    Ad updateAd(Long id, CreateOrUpdateAd request, String email);

    /**
     * Получить объявления пользователя по email
     */
    List<Ad> getMyAds(String email);

    /**
     * Обновляет изображение объявления для пользователя с указанным email
     */
    Ad updateAdImage(Long id, MultipartFile file, String email);
}