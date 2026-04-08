package ru.skypro.homework.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.entity.AdvertisementEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdvertisementRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.impl.AdServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdServiceImplTest {

    @Mock
    private AdvertisementRepository adRepository;

    @Mock
    private AdMapper adMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdServiceImpl adService;

    private AdvertisementEntity adEntity;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setEmail("test@mail.com");

        adEntity = new AdvertisementEntity();
        adEntity.setId(1);
        adEntity.setTitle("Test Ad");
        adEntity.setAuthor(user);
    }

    // ✅ 1. getAllAds
    @Test
    void getAllAds_shouldReturnList() {
        when(adRepository.findAll()).thenReturn(List.of(adEntity));
        when(adMapper.toDto(adEntity)).thenReturn(new Ad());

        List<Ad> result = adService.getAllAds();

        assertEquals(1, result.size());
        verify(adRepository, times(1)).findAll();
    }

    // ✅ 2. getAdById (успешно)
    @Test
    void getAdById_shouldReturnAd() {
        when(adRepository.findById(1L)).thenReturn(Optional.of(adEntity));
        when(adMapper.toDto(adEntity)).thenReturn(new Ad());

        Ad result = adService.getAdById(1L);

        assertNotNull(result);
    }

    // ✅ 3. getAdById (не найден)
    @Test
    void getAdById_shouldThrowException() {
        when(adRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adService.getAdById(1L));
    }

    // ✅ 4. createAd
    @Test
    void createAd_shouldCreateAd() throws IOException {
        CreateOrUpdateAd dto = new CreateOrUpdateAd();

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "image-content".getBytes()
        );

        when(adMapper.toEntity(dto)).thenReturn(adEntity);
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(adRepository.save(any())).thenReturn(adEntity);
        when(adMapper.toDto(adEntity)).thenReturn(new Ad());

        Ad result = adService.createAd(dto, file, "test@mail.com");

        assertNotNull(result);
        verify(adRepository).save(any());
    }

    // ✅ 5. deleteAd
    @Test
    void deleteAd_shouldDelete() {
        when(adRepository.findById(1L)).thenReturn(Optional.of(adEntity));

        adService.deleteAd(1L);

        verify(adRepository).delete(adEntity);
    }

    // ✅ 6. updateAd
    @Test
    void updateAd_shouldUpdateFields() {
        CreateOrUpdateAd dto = new CreateOrUpdateAd();
        dto.setTitle("Updated");
        dto.setPrice(100);

        when(adRepository.findById(1L)).thenReturn(Optional.of(adEntity));
        when(adRepository.save(any())).thenReturn(adEntity);

        AdvertisementEntity result = adService.updateAd(1L, dto);

        assertEquals("Updated", result.getTitle());
        assertEquals(100, result.getPrice());
    }

    // ✅ 7. updateAd (not found)
    @Test
    void updateAd_shouldThrowException() {
        when(adRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> adService.updateAd(1L, new CreateOrUpdateAd()));
    }

    // ✅ 8. updateAdImage
    @Test
    void updateAdImage_shouldUpdateImage() {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "image-content".getBytes()
        );

        when(adRepository.findById(1L)).thenReturn(Optional.of(adEntity));
        when(adRepository.save(any())).thenReturn(adEntity);

        AdvertisementEntity result = adService.updateAdImage(1L, file);

        assertNotNull(result.getImage());
    }

    // ✅ 9. updateAdImage (empty file)
    @Test
    void updateAdImage_shouldThrowIfEmpty() {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        when(adRepository.findById(1L)).thenReturn(Optional.of(adEntity));

        assertThrows(ResponseStatusException.class,
                () -> adService.updateAdImage(1L, file));
    }

    // ✅ 10. isAuthor
    @Test
    void isAuthor_shouldReturnTrue() {
        when(adRepository.findById(1L)).thenReturn(Optional.of(adEntity));

        boolean result = adService.isAuthor(1L, "test@mail.com");

        assertTrue(result);
    }

    // ✅ 11. isAuthor false
    @Test
    void isAuthor_shouldReturnFalse() {
        when(adRepository.findById(1L)).thenReturn(Optional.of(adEntity));

        boolean result = adService.isAuthor(1L, "other@mail.com");

        assertFalse(result);
    }
}
