package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/ads/{adId}/comments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Комментарии", description = "Управление комментариями объявлений")
public class CommentController {

    /**
     * GET /ads/{adId}/comments — Получение комментариев объявления
     */
    @Operation(summary = "Получение комментариев объявления", operationId = "getComments")
    @ApiResponse(responseCode = "200", description = "Успешное получение комментариев",
            content = @Content(schema = @Schema(implementation = Comment.class)))
    @ApiResponse(responseCode = "401", description = "Не авторизован")
    @GetMapping
    public ResponseEntity<List<Comment>> getComments(
            @Parameter(description = "ID объявления", required = true)
            @PathVariable Long adId) {
        // TODO: Реализация получения комментариев по adId
        return ResponseEntity.ok(List.of());
    }

    /**
     * POST /ads/{adId}/comments — Добавление комментария к объявлению
     */
    @Operation(summary = "Добавление комментария", operationId = "addComment")
    @ApiResponse(responseCode = "201", description = "Комментарий создан",
            content = @Content(schema = @Schema(implementation = Comment.class)))
    @ApiResponse(responseCode = "401", description = "Не авторизован")
    @PostMapping
    public ResponseEntity<Comment> addComment(
            @Parameter(description = "ID объявления", required = true)
            @PathVariable Long adId,
            @Parameter(description = "Данные комментария", required = true)
            @Valid @RequestBody CreateOrUpdateComment commentRequest) {
        // TODO: Реализация добавления комментария
        Comment newComment = new Comment();
        return ResponseEntity.status(201).body(newComment);
    }

    /**
     * PATCH /ads/{adId}/comments/{commentId} — Обновление комментария
     */
    @Operation(summary = "Обновление комментария", operationId = "updateComment")
    @ApiResponse(responseCode = "200", description = "Комментарий обновлен",
            content = @Content(schema = @Schema(implementation = Comment.class)))
    @ApiResponse(responseCode = "401", description = "Не авторизован")
    @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    @PatchMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @Parameter(description = "ID объявления", required = true)
            @PathVariable Long adId,
            @Parameter(description = "ID комментария", required = true)
            @PathVariable Integer commentId,
            @Parameter(description = "Данные для обновления", required = true)
            @Valid @RequestBody CreateOrUpdateComment commentRequest) {
        // TODO: Реализация обновления комментария
        Comment updatedComment = new Comment();
        return ResponseEntity.ok(updatedComment);
    }

    /**
     * DELETE /ads/{adId}/comments/{commentId} — Удаление комментария
     */
    @Operation(summary = "Удаление комментария", operationId = "deleteComment")
    @ApiResponse(responseCode = "204", description = "Комментарий удален")
    @ApiResponse(responseCode = "401", description = "Не авторизован")
    @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID объявления", required = true)
            @PathVariable Long adId,
            @Parameter(description = "ID комментария", required = true)
            @PathVariable Integer commentId) {
        // TODO: Реализация удаления комментария
        return ResponseEntity.noContent().build();
    }
}