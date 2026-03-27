package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
@Validated
@Tag(name = "Объявления", description = "Управление объявлениями")
public class AdvertisementController {

    /**
    GET /ads — Получение всех объявлений
     */
    @Operation(summary = "Получение всех объявлений", operationId = "getAllAds")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка объявлений",
            content = @Content(schema = @Schema(implementation = Ad.class)))
    @GetMapping
    public ResponseEntity<List<Ad>> getAllAds() {
        // TODO: Реализация получения всех объявлений
        return ResponseEntity.ok(List.of());
    }

    /**
    POST /ads — Создание объявления
    */
    @Operation(summary = "Создание объявления", operationId = "createAd")
    @ApiResponse(responseCode = "201", description = "Объявление создано",
            content = @Content(schema = @Schema(implementation = Ad.class)))
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Ad> createAd(
            @Parameter(description = "Данные объявления", required = true)
            @RequestPart("properties") @Valid CreateOrUpdateAd properties,
            @Parameter(description = "Изображение объявления", required = true)
            @RequestPart("image") MultipartFile image) {
        // TODO: Реализация создания объявления
        return ResponseEntity.status(201).body(new Ad());
    }

    /**
    GET /ads/{id} — Получение информации об объявлении
     */
    @Operation(summary = "Получение объявления по ID", operationId = "getAdById")
    @ApiResponse(responseCode = "200", description = "Успешное получение объявления",
            content = @Content(schema = @Schema(implementation = Ad.class)))
    @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    @GetMapping("/{id}")
    public ResponseEntity<Ad> getAdById(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable Integer id) {
        // TODO: Реализация получения объявления по id
        return ResponseEntity.ok(new Ad());
    }

    /**
    DELETE /ads/{id} — Удаление объявления
     */
    @Operation(summary = "Удаление объявления по ID", operationId = "deleteAd")
    @ApiResponse(responseCode = "204", description = "Объявление удалено")
    @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable Integer id) {
        // TODO: Реализация удаления объявления
        return ResponseEntity.ok().build();
    }

    /**
    PATCH /ads/{id} — Обновление информации об объявлении
     */
    @Operation(summary = "Обновление объявления по ID", operationId = "updateAd")
    @ApiResponse(responseCode = "200", description = "Объявление обновлено",
            content = @Content(schema = @Schema(implementation = Ad.class)))
    @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    @PatchMapping("/{id}")
    public ResponseEntity<Ad> updateAd(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Данные для обновления", required = true)
            @Valid @RequestBody CreateOrUpdateAd request) {
        // TODO: Реализация обновления объявления
        return ResponseEntity.ok(new Ad());
    }

    /**
    GET /ads/me — Получение объявлений авторизованного пользователя
     */
    @Operation(summary = "Получение объявлений текущего пользователя", operationId = "getMyAds")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка объявлений",
            content = @Content(schema = @Schema(implementation = Ad.class)))
    @GetMapping("/me")
    public ResponseEntity<List<Ad>> getMyAds() {
        // TODO: Реализация получения объявлений текущего пользователя
        return ResponseEntity.ok(List.of());
    }
    /**
     * PATCH /ads/{id}/image - Обновление картинки объявления
     */

    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Обновление картинки объявления",
            operationId = "updateImage",
            tags = {"Объявления"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    schema = @Schema(type = "array", format = "binary")
            )
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<byte[]> updateImage(
            @Parameter(description = "ID объявления", required = true, example = "123")
            @PathVariable Integer id,

            @RequestParam("image") MultipartFile file) {

        // TODO: Реализация логики сохранения изображения

        // Пустой ответ-плейсхолдер
        return ResponseEntity.ok().build();
    }
}