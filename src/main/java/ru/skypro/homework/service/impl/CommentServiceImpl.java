package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.AdvertisementEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdvertisementRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;

    public CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper, UserRepository userRepository, AdvertisementRepository advertisementRepository) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userRepository = userRepository;
        this.advertisementRepository = advertisementRepository;
    }

    @Override
    public List<Comment> getCommentsByAdId(Long adId) {
        List<CommentEntity> commentEntities = commentRepository.findByAdId(adId);
        return commentEntities.stream()
                .map(commentMapper::toDto)  // используем Mapper
                .toList();
    }



    @Override
    public Comment addComment(Long adId, CreateOrUpdateComment commentRequest, Long authorId) {
        AdvertisementEntity ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        UserEntity author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        CommentEntity entity = new CommentEntity();
        entity.setText(commentRequest.getText());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setAuthor(author);
        entity.setAd(ad);

        CommentEntity saved = commentRepository.save(entity);
        return commentMapper.toDto(saved);
    }
    @Override
    public Comment updateComment(Long adId, Long commentId, CreateOrUpdateComment commentRequest, Integer authorId) {
        // Находим комментарий
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));

        // Проверяем, что комментарий принадлежит нужному объявлению
        if (!comment.getAd().getId().equals(adId)) {
            throw new RuntimeException("Комментарий не принадлежит данному объявлению");
        }

        // Проверяем, что текущий пользователь — автор комментария
        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("Нет прав для редактирования комментария");
        }

        // Обновляем текст
        comment.setText(commentRequest.getText());

        // Сохраняем обновлённый комментарий
        CommentEntity updated = commentRepository.save(comment);

        return commentMapper.toDto(updated);
    }
    @Override
    public void deleteComment(Long adId, Long commentId, Long authorId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));

        // Проверяем принадлежность к объявлению
        if (!comment.getAd().getId().equals(adId)) {
            throw new RuntimeException("Комментарий не принадлежит данному объявлению");
        }

        // Проверяем авторство
        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("Нет прав для удаления комментария");
        }

        commentRepository.delete(comment);
    }

}