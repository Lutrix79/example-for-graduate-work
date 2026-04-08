package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.config.TestSecurityConfig;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(TestSecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private Authentication authentication;

    @Autowired
    private ObjectMapper objectMapper;

    private final Integer AD_ID = 1;
    private final Integer COMMENT_ID = 1;
    private final String USERNAME = "user@example.com";

    @Test
    void getComments_success() throws Exception {
        Comment comment = new Comment();
        comment.setPk(COMMENT_ID);
        comment.setAuthor(1);
        comment.setAuthorImage("/users/1/image");
        comment.setAuthorFirstName("John");
        comment.setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)); // Convert LocalDateTime to Long
        comment.setText("Test comment");

        List<Comment> comments = Collections.singletonList(comment);

        when(commentService.getCommentsByAdId(AD_ID)).thenReturn(comments);

        mockMvc.perform(get("/ads/{adId}/comments", AD_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pk").value(COMMENT_ID))
                .andExpect(jsonPath("$[0].text").value("Test comment"));
    }

    @Test
    void addComment_success() throws Exception {
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText("New comment");

        Comment newComment = new Comment();
        newComment.setPk(COMMENT_ID);
        newComment.setAuthor(1);
        newComment.setAuthorImage("/users/1/image");
        newComment.setAuthorFirstName("John");
        newComment.setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)); // Convert LocalDateTime to Long
        newComment.setText("New comment");

        when(commentService.addComment(eq(AD_ID), any(CreateOrUpdateComment.class), eq(USERNAME)))
                .thenReturn(newComment);

        mockMvc.perform(post("/ads/{adId}/comments", AD_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrUpdateComment))
                        .with(user(USERNAME)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pk").value(COMMENT_ID))
                .andExpect(jsonPath("$.text").value("New comment"));
    }

    @Test
    void updateComment_success() throws Exception {
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText("Updated comment");

        Comment updatedComment = new Comment();
        updatedComment.setPk(COMMENT_ID);
        updatedComment.setAuthor(1);
        updatedComment.setAuthorImage("/users/1/image");
        updatedComment.setAuthorFirstName("John");
        updatedComment.setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)); // Convert LocalDateTime to Long
        updatedComment.setText("Updated comment");

        when(commentService.updateComment(eq(AD_ID), eq(COMMENT_ID), any(CreateOrUpdateComment.class)))
                .thenReturn(updatedComment);

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", AD_ID, COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrUpdateComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pk").value(COMMENT_ID))
                .andExpect(jsonPath("$.text").value("Updated comment"));
    }

    @Test
    void deleteComment_success() throws Exception {
        doNothing().when(commentService).deleteComment(AD_ID, COMMENT_ID);

        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", AD_ID, COMMENT_ID))
                .andExpect(status().isNoContent());
    }
}
