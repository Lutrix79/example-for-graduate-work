package ru.skypro.homework.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.RoleEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.RoleRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.impl.UserServiceImpl;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity userEntity;
    private User userDto;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setEmail("test@mail.com");
        userEntity.setPassword("encodedPassword");
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        userEntity.setPhone("1234567890");

        userDto = new User();
        userDto.setEmail("test@mail.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setPhone("1234567890");
    }

    // ✅ changePassword - успешная смена
    @Test
    void changePassword_shouldChangePasswordSuccessfully() {
        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword("oldPassword");
        newPassword.setNewPassword("newPassword");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNew");

        userService.changePassword(newPassword, "test@mail.com");

        verify(userRepository).save(userEntity);
        assertEquals("encodedNew", userEntity.getPassword());
    }

    // ✅ changePassword - неверный текущий пароль
    @Test
    void changePassword_shouldThrow_whenCurrentPasswordWrong() {
        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword("wrong");
        newPassword.setNewPassword("newPassword");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class, () ->
                userService.changePassword(newPassword, "test@mail.com"));
    }

    // ✅ getCurrentUser
    @Test
    void getCurrentUser_shouldReturnUserDto() {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        User result = userService.getCurrentUser("test@mail.com");

        assertEquals(userDto, result);
    }

    // ✅ updateUser
    @Test
    void updateUser_shouldUpdateFields() {
        UpdateUser updateUser = UpdateUser.builder()
                .firstName("Jane")
                .lastName("Smith")
                .phone("0987654321")
                .build();

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));

        UpdateUser result = userService.updateUser(updateUser, "test@mail.com");

        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("0987654321", result.getPhone());
        verify(userRepository).save(userEntity);
    }

    // ✅ updateUserImage - успешная загрузка
    @Test
    void updateUserImage_shouldSaveFileAndUpdateUser() throws IOException {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("avatar.png");

        userService.updateUserImage(multipartFile, "test@mail.com");

        verify(userRepository).save(userEntity);
        assertTrue(userEntity.getImage().contains("avatar.png"));
    }

    // ✅ updateUserImage - файл пустой
    @Test
    void updateUserImage_shouldThrowIfFileEmpty() {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));
        when(multipartFile.isEmpty()).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
                userService.updateUserImage(multipartFile, "test@mail.com"));
    }

    // ✅ registerUser - успешная регистрация
    @Test
    void registerUser_shouldSaveUser() {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(userDto)).thenReturn(userEntity);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new RoleEntity(1, "USER")));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.registerUser(userDto, "password");

        verify(userRepository).save(userEntity);
        assertTrue(userEntity.getEnabled());
    }

    // ✅ registerUser - email уже существует
    @Test
    void registerUser_shouldThrowIfEmailExists() {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));

        assertThrows(ResponseStatusException.class, () ->
                userService.registerUser(userDto, "password"));
    }
}
