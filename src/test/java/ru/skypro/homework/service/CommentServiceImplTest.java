package ru.skypro.homework.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.AdvertisementEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdvertisementRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.service.impl.CommentServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdvertisementRepository advertisementRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private UserEntity user;
    private AdvertisementEntity ad;
    private CommentEntity commentEntity;
    private CreateOrUpdateComment commentRequest;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setEmail("test@mail.com");

        ad = new AdvertisementEntity();
        ad.setId(1);

        commentEntity = new CommentEntity();
        commentEntity.setId(1);
        commentEntity.setAd(ad);
        commentEntity.setAuthor(user);
        commentEntity.setText("Old text");
        commentEntity.setCreatedAt(LocalDateTime.now());

        commentRequest = new CreateOrUpdateComment();
        commentRequest.setText("New comment text");
    }

    // ✅ getCommentsByAdId
    @Test
    void getCommentsByAdId_shouldReturnList() {
        when(commentRepository.findByAdId(1)).thenReturn(List.of(commentEntity));
        when(commentMapper.toDto(commentEntity)).thenReturn(new Comment());

        List<Comment> result = commentService.getCommentsByAdId(1);

        assertEquals(1, result.size());
        verify(commentRepository).findByAdId(1);
    }

    // ✅ addComment - успешно
    @Test
    void addComment_shouldReturnComment() {
        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(ad));
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(commentMapper.toEntity(commentRequest)).thenReturn(commentEntity);
        when(commentRepository.save(commentEntity)).thenReturn(commentEntity);
        when(commentMapper.toDto(commentEntity)).thenReturn(new Comment());

        Comment result = commentService.addComment(1, commentRequest, "test@mail.com");

        assertNotNull(result);
        assertEquals(ad, commentEntity.getAd());
        assertEquals(user, commentEntity.getAuthor());
    }

    // ✅ addComment - объявление не найдено
    @Test
    void addComment_shouldThrowIfAdNotFound() {
        when(advertisementRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> commentService.addComment(1, commentRequest, "test@mail.com"));
    }

    // ✅ addComment - пользователь не найден
    @Test
    void addComment_shouldThrowIfUserNotFound() {
        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(ad));
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> commentService.addComment(1, commentRequest, "test@mail.com"));
    }

    // ✅ updateComment - успешно
    @Test
    void updateComment_shouldUpdateText() {
        when(commentRepository.findById(1)).thenReturn(Optional.of(commentEntity));
        when(commentRepository.save(commentEntity)).thenReturn(commentEntity);
        when(commentMapper.toDto(commentEntity)).thenReturn(new Comment());

        Comment result = commentService.updateComment(1, 1, commentRequest);

        assertNotNull(result);
        assertEquals("New comment text", commentEntity.getText());
    }

    // ✅ updateComment - комментарий не найден
    @Test
    void updateComment_shouldThrowIfCommentNotFound() {
        when(commentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> commentService.updateComment(1, 1, commentRequest));
    }

    // ✅ updateComment - комментарий не принадлежит объявлению
    @Test
    void updateComment_shouldThrowIfAdMismatch() {
        AdvertisementEntity otherAd = new AdvertisementEntity();
        otherAd.setId(2);
        commentEntity.setAd(otherAd);

        when(commentRepository.findById(1)).thenReturn(Optional.of(commentEntity));

        assertThrows(ResponseStatusException.class,
                () -> commentService.updateComment(1, 1, commentRequest));
    }

    // ✅ deleteComment - успешно
    @Test
    void deleteComment_shouldDelete() {
        when(commentRepository.findById(1)).thenReturn(Optional.of(commentEntity));

        commentService.deleteComment(1, 1);

        verify(commentRepository).delete(commentEntity);
    }

    // ✅ deleteComment - комментарий не найден
    @Test
    void deleteComment_shouldThrowIfCommentNotFound() {
        when(commentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> commentService.deleteComment(1, 1));
    }

    // ✅ deleteComment - комментарий не принадлежит объявлению
    @Test
    void deleteComment_shouldThrowIfAdMismatch() {
        AdvertisementEntity otherAd = new AdvertisementEntity();
        otherAd.setId(2);
        commentEntity.setAd(otherAd);

        when(commentRepository.findById(1)).thenReturn(Optional.of(commentEntity));

        assertThrows(ResponseStatusException.class,
                () -> commentService.deleteComment(1, 1));
    }
}
