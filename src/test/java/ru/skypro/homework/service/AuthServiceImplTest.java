package ru.skypro.homework.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.entity.RoleEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.RoleRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.impl.AuthServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserDetailsManager userDetailsManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserEntity userEntity;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        roleEntity = new RoleEntity();
        roleEntity.setName("USER");

        userEntity = new UserEntity();
        userEntity.setEmail("test@mail.com");
        userEntity.setPassword("encodedPassword");
    }

    // ✅ login - пользователь существует и пароль совпадает
    @Test
    void login_shouldReturnTrue_whenPasswordMatches() {
        Register register = new Register();
        register.setUsername("test@mail.com");
        register.setPassword("123456");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsManager.loadUserByUsername("test@mail.com")).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("encodedPassword");
        when(passwordEncoder.matches("123456", "encodedPassword")).thenReturn(true);

        boolean result = authService.login("test@mail.com", "123456");

        assertTrue(result);
    }

    // ✅ login - пользователь не найден
    @Test
    void login_shouldReturnFalse_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());

        boolean result = authService.login("unknown@mail.com", "123");

        assertFalse(result);
    }

    // ✅ login - пароль не совпадает
    @Test
    void login_shouldReturnFalse_whenPasswordDoesNotMatch() {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsManager.loadUserByUsername("test@mail.com")).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("encodedPassword");
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        boolean result = authService.login("test@mail.com", "wrongPassword");

        assertFalse(result);
    }

    // ✅ register - успешная регистрация
    @Test
    void register_shouldReturnTrue_whenUserNotExists() {
        Register register = new Register();
        register.setUsername("new@mail.com");
        register.setPassword("123456");
        register.setFirstName("John");
        register.setLastName("Doe");

        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(roleEntity));
        when(passwordEncoder.encode("123456")).thenReturn("encoded123");

        boolean result = authService.register(register);

        assertTrue(result);
        verify(userRepository).save(any(UserEntity.class));
    }

    // ✅ register - пользователь уже существует
    @Test
    void register_shouldReturnFalse_whenUserExists() {
        Register register = new Register();
        register.setUsername("test@mail.com");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));

        boolean result = authService.register(register);

        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    // ✅ register - роль не найдена
    @Test
    void register_shouldThrow_whenRoleNotFound() {
        Register register = new Register();
        register.setUsername("new@mail.com");
        register.setPassword("123456");

        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.register(register));
    }
}
