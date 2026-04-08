package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.config.TestSecurityConfig;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.entity.AdvertisementEntity;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.service.AdService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdvertisementController.class, excludeAutoConfiguration = {})
@Import(TestSecurityConfig.class)
class AdvertisementControllerTest {

    @MockBean
    private AdService adService;

    @MockBean
    private AdMapper adMapper;

    @Autowired
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;
    private Authentication authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        userDetails = User.builder()
                .username("testuser@example.com")
                .password("password")
                .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
        
        authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void getAllAds_ShouldReturnListOfAds() throws Exception {
        List<Ad> ads = Arrays.asList(
                createTestAd(1, 1, "http://example.com/image1.jpg", 1000, "Test Ad 1"),
                createTestAd(2, 2, "http://example.com/image2.jpg", 2000, "Test Ad 2")
        );

        when(adService.getAllAds()).thenReturn(ads);

        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].pk").value(1))
                .andExpect(jsonPath("$[0].author").value(1))
                .andExpect(jsonPath("$[0].price").value(1000))
                .andExpect(jsonPath("$[0].title").value("Test Ad 1"))
                .andExpect(jsonPath("$[1].pk").value(2))
                .andExpect(jsonPath("$[1].author").value(2))
                .andExpect(jsonPath("$[1].price").value(2000))
                .andExpect(jsonPath("$[1].title").value("Test Ad 2"));

        verify(adService, times(1)).getAllAds();
    }

    @Test
    void createAd_ShouldReturnCreatedAd() throws Exception {
        CreateOrUpdateAd createAd = new CreateOrUpdateAd();
        createAd.setTitle("New Ad");
        createAd.setPrice(1500);
        createAd.setDescription("Test description");

        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        Ad createdAd = createTestAd(3, 1, "http://example.com/image3.jpg", 1500, "New Ad");

        when(adService.createAd(any(CreateOrUpdateAd.class), any(MultipartFile.class), anyString()))
                .thenReturn(createdAd);

        mockMvc.perform(multipart("/ads")
                        .file(image)
                        .file(new MockMultipartFile("properties", "", "application/json",
                                objectMapper.writeValueAsString(createAd).getBytes()))
                        .with(authentication(authentication)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pk").value(3))
                .andExpect(jsonPath("$.author").value(1))
                .andExpect(jsonPath("$.price").value(1500))
                .andExpect(jsonPath("$.title").value("New Ad"));

        verify(adService, times(1)).createAd(any(CreateOrUpdateAd.class), any(MultipartFile.class), eq("testuser@example.com"));
    }

    @Test
    void getAdById_ShouldReturnAd() throws Exception {
        Ad ad = createTestAd(1, 1, "http://example.com/image1.jpg", 1000, "Test Ad");

        when(adService.getAdById(1L)).thenReturn(ad);

        mockMvc.perform(get("/ads/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.author").value(1))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.title").value("Test Ad"));

        verify(adService, times(1)).getAdById(1L);
    }

    @Test
    void deleteAd_ShouldReturnNoContent() throws Exception {
        doNothing().when(adService).deleteAd(1L);

        mockMvc.perform(delete("/ads/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(adService, times(1)).deleteAd(1L);
    }

    @Test
    void updateAd_ShouldReturnUpdatedAd() throws Exception {
        CreateOrUpdateAd updateAd = new CreateOrUpdateAd();
        updateAd.setTitle("Updated Ad");
        updateAd.setPrice(2500);
        updateAd.setDescription("Updated description");

        AdvertisementEntity updatedEntity = new AdvertisementEntity();
        updatedEntity.setId(1);

        Ad updatedAd = createTestAd(1, 1, "http://example.com/image1.jpg", 2500, "Updated Ad");

        when(adService.updateAd(eq(1L), any(CreateOrUpdateAd.class))).thenReturn(updatedEntity);
        when(adMapper.toDto(updatedEntity)).thenReturn(updatedAd);

        mockMvc.perform(patch("/ads/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAd)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.price").value(2500))
                .andExpect(jsonPath("$.title").value("Updated Ad"));

        verify(adService, times(1)).updateAd(eq(1L), any(CreateOrUpdateAd.class));
        verify(adMapper, times(1)).toDto(updatedEntity);
    }

    @Test
    void getMyAds_ShouldReturnUserAds() throws Exception {
        AdvertisementEntity adEntity1 = new AdvertisementEntity();
        adEntity1.setId(1);
        AdvertisementEntity adEntity2 = new AdvertisementEntity();
        adEntity2.setId(2);
        List<AdvertisementEntity> userAds = Arrays.asList(
                adEntity1,
                adEntity2
        );

        List<Ad> mappedAds = Arrays.asList(
                createTestAd(1, 1, "http://example.com/image1.jpg", 1000, "My Ad 1"),
                createTestAd(2, 1, "http://example.com/image2.jpg", 2000, "My Ad 2")
        );

        when(adService.getMyAds("testuser@example.com")).thenReturn(userAds);
        when(adMapper.toDto(any(AdvertisementEntity.class)))
                .thenReturn(mappedAds.get(0), mappedAds.get(1));

        mockMvc.perform(get("/ads/me")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].pk").value(1))
                .andExpect(jsonPath("$[0].author").value(1))
                .andExpect(jsonPath("$[0].title").value("My Ad 1"))
                .andExpect(jsonPath("$[1].pk").value(2))
                .andExpect(jsonPath("$[1].author").value(1))
                .andExpect(jsonPath("$[1].title").value("My Ad 2"));

        verify(adService, times(1)).getMyAds("testuser@example.com");
        verify(adMapper, times(2)).toDto(any(AdvertisementEntity.class));
    }

    @Test
    void updateImage_ShouldReturnUpdatedAd() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "updated.jpg", MediaType.IMAGE_JPEG_VALUE, "updated image content".getBytes());

        AdvertisementEntity updatedEntity = new AdvertisementEntity();
        updatedEntity.setId(1);

        Ad updatedAd = createTestAd(1, 1, "http://example.com/updated.jpg", 1000, "Test Ad");

        when(adService.updateAdImage(eq(1L), any(MultipartFile.class))).thenReturn(updatedEntity);
        when(adMapper.toDto(updatedEntity)).thenReturn(updatedAd);

        mockMvc.perform(multipart("/ads/{id}/image", 1L)
                        .file(image)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.image").value("http://example.com/updated.jpg"));

        verify(adService, times(1)).updateAdImage(eq(1L), any(MultipartFile.class));
        verify(adMapper, times(1)).toDto(updatedEntity);
    }

    @Test
    void createAd_WithValidData_ShouldReturnCreated() throws Exception {
        CreateOrUpdateAd validAd = new CreateOrUpdateAd();
        validAd.setTitle("Valid Title");
        validAd.setPrice(1000);
        validAd.setDescription("Valid description");

        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        Ad createdAd = createTestAd(3, 1, "http://example.com/image3.jpg", 1000, "Valid Title");

        when(adService.createAd(any(CreateOrUpdateAd.class), any(MultipartFile.class), anyString()))
                .thenReturn(createdAd);

        mockMvc.perform(multipart("/ads")
                        .file(image)
                        .file(new MockMultipartFile("properties", "", "application/json",
                                objectMapper.writeValueAsString(validAd).getBytes()))
                        .with(authentication(authentication)))
                .andExpect(status().isCreated());

        verify(adService, times(1)).createAd(any(CreateOrUpdateAd.class), any(MultipartFile.class), eq("testuser@example.com"));
    }

    @Test
    void updateAd_WithValidData_ShouldReturnUpdated() throws Exception {
        CreateOrUpdateAd validAd = new CreateOrUpdateAd();
        validAd.setTitle("Valid Updated Title");
        validAd.setPrice(2500);
        validAd.setDescription("Valid updated description");

        AdvertisementEntity updatedEntity = new AdvertisementEntity();
        updatedEntity.setId(1);

        Ad updatedAd = createTestAd(1, 1, "http://example.com/image1.jpg", 2500, "Valid Updated Title");

        when(adService.updateAd(eq(1L), any(CreateOrUpdateAd.class))).thenReturn(updatedEntity);
        when(adMapper.toDto(updatedEntity)).thenReturn(updatedAd);

        mockMvc.perform(patch("/ads/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAd)))
                .andExpect(status().isOk());

        verify(adService, times(1)).updateAd(eq(1L), any(CreateOrUpdateAd.class));
        verify(adMapper, times(1)).toDto(updatedEntity);
    }

    private Ad createTestAd(Integer pk, Integer author, String image, Integer price, String title) {
        Ad ad = new Ad();
        ad.setPk(pk);
        ad.setAuthor(author);
        ad.setImage(image);
        ad.setPrice(price);
        ad.setTitle(title);
        return ad;
    }
}
