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

import com.heartsave.todaktodak_api.common.BaseTestObject;
import com.heartsave.todaktodak_api.common.security.WithMockTodakUser;
import com.heartsave.todaktodak_api.diary.entity.DiaryEntity;
import com.heartsave.todaktodak_api.diary.entity.projection.DiaryReactionCountProjection;
import com.heartsave.todaktodak_api.diary.entity.projection.MySharedDiaryContentOnlyProjection;
import com.heartsave.todaktodak_api.diary.entity.projection.MySharedDiaryPreviewProjection;
import com.heartsave.todaktodak_api.diary.repository.DiaryReactionRepository;
import com.heartsave.todaktodak_api.diary.repository.MySharedDiaryRepository;
import com.heartsave.todaktodak_api.listener.ContextLoadTimeTestExecutionListener;
import com.heartsave.todaktodak_api.member.entity.MemberEntity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Import(IntegrateTestConfiguration.class)
@TestExecutionListeners(
    value = ContextLoadTimeTestExecutionListener.class,
    mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public class MySharedDiaryTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private MySharedDiaryRepository mySharedDiaryRepository;

  @Autowired private DiaryReactionRepository reactionRepository;

  private MemberEntity member;

  @BeforeEach
  void setup() {
    assertThat(mockingDetails(mySharedDiaryRepository).isMock()).isTrue();
    assertThat(mockingDetails(reactionRepository).isMock()).isTrue();
    member = createMember();
  }

  @Test
  @WithMockTodakUser
  public void testGetMySharedDiaryPagination() throws Exception {
    Long afterId = 0L;
    DiaryEntity diary = BaseTestObject.createDiaryWithMember(member);

    MySharedDiaryPreviewProjection preview = mock(MySharedDiaryPreviewProjection.class);
    when(preview.getPublicDiaryId()).thenReturn(1L);
    when(preview.getWebtoonImageUrl()).thenReturn(diary.getWebtoonImageUrl());
    when(preview.getCreatedDate()).thenReturn(diary.getDiaryCreatedTime().toLocalDate());

    List<MySharedDiaryPreviewProjection> previews = Arrays.asList(preview);

    when(mySharedDiaryRepository.findLatestId(member.getId())).thenReturn(Optional.of(1L));
    when(mySharedDiaryRepository.findNextPreviews(anyLong(), anyLong(), any(PageRequest.class)))
        .thenReturn(previews);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/diary/my/shared")
                .param("after", String.valueOf(afterId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sharedDiaries").isArray())
        .andExpect(jsonPath("$.after").exists())
        .andExpect(jsonPath("$.isEnd").exists());
  }

  @Test
  @WithMockTodakUser
  public void testGetMySharedDiary() throws Exception {

    LocalDateTime requestDate = LocalDateTime.now();

    DiaryEntity diary = createDiaryWithMember(member);
    MySharedDiaryContentOnlyProjection contentOnly = mock(MySharedDiaryContentOnlyProjection.class);
    when(contentOnly.getDiaryId()).thenReturn(diary.getId());
    when(contentOnly.getPublicDiaryId()).thenReturn(1L);
    when(contentOnly.getPublicContent()).thenReturn("public-content");
    when(contentOnly.getDiaryCreatedDate()).thenReturn(diary.getDiaryCreatedTime().toLocalDate());
    when(contentOnly.getBgmUrl()).thenReturn(diary.getBgmUrl());
    when(contentOnly.getWebtoonImageUrls()).thenReturn(List.of(diary.getWebtoonImageUrl()));

    DiaryReactionCountProjection reactionCount = createReactionCount();

    when(mySharedDiaryRepository.findContentOnly(anyLong(), any()))
        .thenReturn(Optional.of(contentOnly));
    when(reactionRepository.countEachByDiaryId(anyLong())).thenReturn(reactionCount);
    when(reactionRepository.findMemberReaction(anyLong(), anyLong())).thenReturn(Arrays.asList());

    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/diary/my/shared/detail")
                    .param("date", requestDate.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.publicDiaryId").value(1))
            .andExpect(jsonPath("$.webtoonImageUrls[0]").exists())
            .andExpect(jsonPath("$.publicContent").value("public-content"))
            .andExpect(jsonPath("$.bgmUrl").exists())
            .andExpect(jsonPath("$.reactionCount").exists())
            .andExpect(jsonPath("$.myReaction").exists())
            .andExpect(jsonPath("$.diaryCreatedDate").value(requestDate.toLocalDate().toString()))
            .andDo(print())
            .andReturn();

    log.info("response={}", mvcResult.getResponse().getContentAsString());
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
