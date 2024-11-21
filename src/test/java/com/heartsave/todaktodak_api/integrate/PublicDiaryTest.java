package com.heartsave.todaktodak_api.integrate;

import static com.heartsave.todaktodak_api.common.BaseTestObject.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartsave.todaktodak_api.common.security.WithMockTodakUser;
import com.heartsave.todaktodak_api.diary.constant.DiaryReactionType;
import com.heartsave.todaktodak_api.diary.dto.request.PublicDiaryReactionRequest;
import com.heartsave.todaktodak_api.diary.dto.request.PublicDiaryWriteRequest;
import com.heartsave.todaktodak_api.diary.entity.DiaryEntity;
import com.heartsave.todaktodak_api.diary.entity.projection.DiaryReactionCountProjection;
import com.heartsave.todaktodak_api.diary.entity.projection.PublicDiaryContentOnlyProjection;
import com.heartsave.todaktodak_api.diary.repository.DiaryReactionRepository;
import com.heartsave.todaktodak_api.diary.repository.DiaryRepository;
import com.heartsave.todaktodak_api.diary.repository.PublicDiaryRepository;
import com.heartsave.todaktodak_api.listener.ContextLoadTimeTestExecutionListener;
import com.heartsave.todaktodak_api.member.entity.MemberEntity;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Import(IntegrateTestConfiguration.class)
@TestExecutionListeners(
    value = ContextLoadTimeTestExecutionListener.class,
    mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
@DisplayName("공개 일기 통합 테스트")
public class PublicDiaryTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Autowired private PublicDiaryRepository publicDiaryRepository;
  @Autowired private DiaryRepository diaryRepository;
  @Autowired private DiaryReactionRepository reactionRepository;

  private MemberEntity member;

  @BeforeEach
  void setup() {
    assertThat(mockingDetails(publicDiaryRepository).isMock()).isTrue();
    assertThat(mockingDetails(diaryRepository).isMock()).isTrue();
    assertThat(mockingDetails(reactionRepository).isMock()).isTrue();
    member = createMember();
  }

  @Test
  @WithMockTodakUser
  @DisplayName("공개 일기 목록 조회 - 페이징 처리된 공개 일기와 반응 정보를 정상적으로 반환한다")
  public void testGetPublicDiaries() throws Exception {
    Long afterId = 0L;
    DiaryEntity diary = createDiaryWithMember(member);

    PublicDiaryContentOnlyProjection content = mock(PublicDiaryContentOnlyProjection.class);
    when(content.getDiaryId()).thenReturn(diary.getId());
    when(content.getPublicContent()).thenReturn("public-content");
    when(content.getWebtoonImageUrls()).thenReturn(List.of(diary.getWebtoonImageUrl()));
    when(content.getBgmUrl()).thenReturn(diary.getBgmUrl());
    when(content.getCharacterImageUrl()).thenReturn(member.getCharacterImageUrl());

    List<PublicDiaryContentOnlyProjection> contents = Arrays.asList(content);
    DiaryReactionCountProjection reactionCount = createReactionCount();

    when(publicDiaryRepository.findLatestId()).thenReturn(Optional.of(1L));
    when(publicDiaryRepository.findNextContentOnlyById(anyLong(), any(PageRequest.class)))
        .thenReturn(contents);
    when(reactionRepository.countEachByDiaryId(anyLong())).thenReturn(reactionCount);
    when(reactionRepository.findMemberReaction(anyLong(), anyLong())).thenReturn(Arrays.asList());

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/diary/public")
                .param("after", String.valueOf(afterId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.diaries").isArray())
        .andExpect(jsonPath("$.isEnd").isBoolean())
        .andExpect(jsonPath("$.after").isNumber())
        .andExpect(jsonPath("$.diaries[0].publicDiaryId").isNumber())
        .andExpect(jsonPath("$.diaries[0].diaryId").isNumber())
        .andExpect(jsonPath("$.diaries[0].characterImageUrl").isString())
        .andExpect(jsonPath("$.diaries[0].publicContent").isString())
        .andDo(print());
  }

  @Test
  @WithMockTodakUser
  @DisplayName("공개 일기 작성 - 일기 내용과 공개 설정을 정상적으로 저장한다")
  public void testWritePublicContent() throws Exception {
    DiaryEntity diary = createDiaryWithMember(member);
    when(diaryRepository.findById(anyLong())).thenReturn(Optional.of(diary));

    PublicDiaryWriteRequest request = new PublicDiaryWriteRequest(diary.getId(), "public-content");

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/diary/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(print());
  }

  @Test
  @WithMockTodakUser
  @DisplayName("일기 반응 토글 - 사용자의 반응을 추가하거나 제거한다")
  public void testToggleReaction() throws Exception {
    PublicDiaryReactionRequest request = new PublicDiaryReactionRequest(1L, DiaryReactionType.LIKE);

    when(reactionRepository.hasReaction(anyLong(), anyLong(), any(DiaryReactionType.class)))
        .thenReturn(false);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/diary/public/reaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(print());
  }

  @NotNull
  private DiaryReactionCountProjection createReactionCount() {
    return new DiaryReactionCountProjection() {
      @Override
      public Long getLikes() {
        return 1L;
      }

      @Override
      public Long getSurprised() {
        return 2L;
      }

      @Override
      public Long getEmpathize() {
        return 3L;
      }

      @Override
      public Long getCheering() {
        return 4L;
      }
    };
  }
}
