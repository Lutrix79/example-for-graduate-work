package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.UserService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // отключаем security фильтры
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ 1. Получение текущего пользователя
    @Test
    void getCurrentUser_shouldReturnUser() throws Exception {
        String email = "test@mail.com";

        User user = new User();
        user.setEmail(email);

        when(authentication.getName()).thenReturn(email);
        when(userService.getCurrentUser(email)).thenReturn(user);

        mockMvc.perform(get("/users/me")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    // ✅ 2. Смена пароля
    @Test
    void changePassword_shouldReturnOk() throws Exception {
        String email = "test@mail.com";

        String requestJson = """
                {
                    "currentPassword": "old123",
                    "newPassword": "new123"
                }
                """;

        when(authentication.getName()).thenReturn(email);

        mockMvc.perform(post("/users/set_password")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    // ✅ 3. Обновление пользователя
    @Test
    void updateUserInfo_shouldReturnUpdatedUser() throws Exception {
        String email = "test@mail.com";

        String requestJson = """
                {
                    "firstName": "John",
                    "lastName": "Doe"
                }
                """;

        when(authentication.getName()).thenReturn(email);

        mockMvc.perform(patch("/users/me")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    // ✅ 4. Регистрация пользователя
    @Test
    void registerUser_shouldReturnCreated() throws Exception {

        String requestJson = """
                {
                    "user": {
                        "email": "test@mail.com",
                        "firstName": "John",
                        "lastName": "Doe"
                    },
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());
    }

    // ✅ 5. Обновление аватара
    @Test
    void updateUserImage_shouldReturnOk() throws Exception {
        String email = "test@mail.com";

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image-content".getBytes()
        );

        when(authentication.getName()).thenReturn(email);

        mockMvc.perform(multipart("/users/me/image")
                        .file(file)
                        .principal(authentication)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk());
    }
}