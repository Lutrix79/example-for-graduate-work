package ru.skypro.homework.service;

import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByAdId(Long adId);
    Comment addComment(Long adId, CreateOrUpdateComment commentRequest, Long authorId);
    Comment updateComment(Long adId, Long commentId, CreateOrUpdateComment commentRequest, Integer authorId);
    void deleteComment(Long adId, Long commentId, Long authorId);
}
