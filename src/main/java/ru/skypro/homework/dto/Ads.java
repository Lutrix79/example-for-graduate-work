package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Список объявлений")
public class Ads {

    @Schema(description = "общее количество объявлений",
            example = "10")
    private int count;

    @Schema(description = "список объявлений")
    private Ad result;

    public Ads(int count, Ad result) {
        this.count = count;
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public Ad getResult() {
        return result;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setResult(Ad result) {
        this.result = result;
    }
}
