package com.heartsave.todaktodak_api.diary.dto.request;

import static com.heartsave.todaktodak_api.diary.constant.DiaryContentConstraintConstant.DIARY_CONTENT_MAX_SIZE;
import static com.heartsave.todaktodak_api.diary.constant.DiaryContentConstraintConstant.DIARY_CONTENT_MIN_SIZE;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.heartsave.todaktodak_api.diary.constant.DiaryBgmGenre;
import com.heartsave.todaktodak_api.diary.constant.DiaryEmotion;
import com.heartsave.todaktodak_api.diary.dto.TimeWithinTolerance;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "일기 작성 요청 데이터")
public class DiaryWriteRequest {

  @Schema(
      description = "일기 작성 날짜",
      type = "string",
      format = "date-time",
      example = "2024-10-26T15:30:00")
  @TimeWithinTolerance(message = "Diary Writing Date is Future")
  @NotNull
  @JsonProperty("date")
  private Instant createdTime;

  @Schema(description = "일기에 기록된 감정", example = "happy", required = true)
  @NotNull(message = "DiaryEmotion is Null")
  private DiaryEmotion emotion;

  @Schema(
      description = "일기 내용",
      example = "오늘은 정말 좋은 하루였다...",
      minLength = DIARY_CONTENT_MIN_SIZE,
      maxLength = DIARY_CONTENT_MAX_SIZE)
  @Size(
      min = DIARY_CONTENT_MIN_SIZE,
      max = DIARY_CONTENT_MAX_SIZE,
      message = "Diary Content Length Out Of Range")
  @NotBlank
  private String content;

  @NotNull(message = "DiaryBgmGenre is Null")
  private DiaryBgmGenre bgmGenre;
}
