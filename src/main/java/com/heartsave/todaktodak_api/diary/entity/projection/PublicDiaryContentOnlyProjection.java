package com.heartsave.todaktodak_api.diary.entity.projection;

import com.heartsave.todaktodak_api.common.constant.CoreConstant.TIME_FORMAT;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Schema(description = "공개 일기 조회 프로젝션")
public class PublicDiaryContentOnlyProjection {

  @Schema(description = "공개 일기 ID", example = "1")
  private final Long publicDiaryId;

  @Schema(description = "원본 일기 ID", example = "1")
  private final Long diaryId;

  @Schema(description = "작성자 캐릭터 이미지 URL", example = "character/123")
  private String characterImageUrl;

  @Schema(description = "작성자 닉네임", example = "Todak")
  private final String nickname;

  @Schema(description = "공개된 일기 내용", example = "오늘은 행복한 하루였습니다...")
  private final String publicContent;

  @Schema(
      description = "DB에서 웹툰 이미지 폴더 URL 1개를 가져옵니다. 이후에 pre-signed URL 4개로 변환됩니다.",
      example = "[\"https://example.com/webtoon/1.jpg\", \"https://example.com/webtoon/2.jpg\"]")
  private List<String> webtoonImageUrls;

  @Schema(description = "배경음악 URL", example = "https://example.com/bgm/123.mp3")
  private String bgmUrl;

  @Schema(description = "일기 작성 날짜", example = "2024-10-26", type = "string", format = "date")
  @DateTimeFormat(pattern = TIME_FORMAT.ISO_DATETIME_WITH_MILLISECONDS)
  private final Instant date;

  public PublicDiaryContentOnlyProjection(
      Long publicDiaryId,
      Long diaryId,
      String characterImageUrls,
      String nickname,
      String publicContent,
      String webtoonImageUrl,
      String bgmUrl,
      Instant date) {
    this.publicDiaryId = publicDiaryId;
    this.diaryId = diaryId;
    this.characterImageUrl = characterImageUrls;
    this.nickname = nickname;
    this.publicContent = publicContent;
    this.webtoonImageUrls = List.of(webtoonImageUrl);
    this.bgmUrl = bgmUrl;
    this.date = date;
  }

  public void replaceWebtoonImageUrls(List<String> webtoonImageUrls) {
    this.webtoonImageUrls = webtoonImageUrls;
  }

  public void replaceCharacterImageUrl(String characterImageUrl) {
    this.characterImageUrl = characterImageUrl;
  }

  public void replaceBgmUrl(String bgmUrl) {
    this.bgmUrl = bgmUrl;
  }
}
