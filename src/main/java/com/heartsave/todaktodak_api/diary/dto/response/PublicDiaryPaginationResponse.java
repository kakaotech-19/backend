package com.heartsave.todaktodak_api.diary.dto.response;

import com.heartsave.todaktodak_api.diary.dto.PublicDiary;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Schema(description = "공개 일기 상세 조회 응답")
@Getter
public class PublicDiaryPaginationResponse {

  @Schema(description = "조회된 공개 일기 목록")
  private List<PublicDiary> diaries;

  @Schema(description = "다음 페이지 조회를 위한 마지막 일기 ID", example = "97")
  private Long after;

  @Schema(description = "조회할 수 있는 다음 페이지 존재 여부 (true: 더 이상 조회 불가)", example = "false")
  private Boolean isEnd;

  public PublicDiaryPaginationResponse() {
    this.diaries = new ArrayList<>();
    this.after = 1L;
    this.isEnd = true;
  }

  public void addPublicDiary(PublicDiary publicDiary) {
    diaries.add(publicDiary);
    if (after == 1L) {
      after = publicDiary.getPublicDiaryId();
      isEnd = false;
    } else after = Math.min(publicDiary.getPublicDiaryId(), after);
  }
}
