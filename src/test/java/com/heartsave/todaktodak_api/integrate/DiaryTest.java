package com.heartsave.todaktodak_api.integrate;

import static com.heartsave.todaktodak_api.common.BaseTestObject.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartsave.todaktodak_api.ai.client.dto.response.AiDiaryContentResponse;
import com.heartsave.todaktodak_api.ai.client.service.AiClientService;
import com.heartsave.todaktodak_api.common.BaseTestObject;
import com.heartsave.todaktodak_api.common.security.WithMockTodakUser;
import com.heartsave.todaktodak_api.diary.constant.DiaryEmotion;
import com.heartsave.todaktodak_api.diary.dto.request.DiaryWriteRequest;
import com.heartsave.todaktodak_api.diary.entity.DiaryEntity;
import com.heartsave.todaktodak_api.diary.repository.DiaryRepository;
import com.heartsave.todaktodak_api.listener.ContextLoadTimeTestExecutionListener;
import com.heartsave.todaktodak_api.member.entity.MemberEntity;
import com.heartsave.todaktodak_api.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrateTestConfiguration.class)
@TestExecutionListeners(
    value = ContextLoadTimeTestExecutionListener.class,
    mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public class DiaryTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Autowired private MemberRepository memberRepository;

  @Autowired private DiaryRepository diaryRepository;

  @Autowired private AiClientService aiClientService;

  private MemberEntity member;

  @BeforeEach
  void setup() {
    assertThat(mockingDetails(memberRepository).isMock()).isTrue();
    assertThat(mockingDetails(diaryRepository).isMock()).isTrue();
    assertThat(mockingDetails(aiClientService).isMock()).isTrue();

    System.out.println("memberRepository.hashCode() = " + memberRepository.hashCode());
    System.out.println("diaryRepository.hashCode() = " + diaryRepository.hashCode());
    System.out.println("aiClientService.hashCode() = " + aiClientService.hashCode());
    member = createMember();
  }

  @Test
  @WithMockTodakUser
  public void testWriteDiary() throws Exception {
    DiaryWriteRequest request =
        new DiaryWriteRequest(LocalDateTime.now(), DiaryEmotion.HAPPY, DUMMY_STRING_CONTENT);
    AiDiaryContentResponse contentResponse =
        AiDiaryContentResponse.builder().aiComment("aiComment").build();
    when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
    when(diaryRepository.existsByDate(anyLong(), any(LocalDateTime.class))).thenReturn(false);
    when(aiClientService.callDiaryContent(any(DiaryEntity.class))).thenReturn(contentResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/diary/my")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.aiComment").value("aiComment"));
  }

  @Test
  @WithMockTodakUser
  public void testDeleteDiary() throws Exception {
    DiaryEntity diary = BaseTestObject.createDiaryWithMember(member);
    when(diaryRepository.deleteByIds(member.getId(), diary.getId())).thenReturn(1);

    mockMvc
        .perform(delete("/api/v1/diary/my/{diaryId}", diary.getId()))
        .andExpect(status().isNoContent());
  }

  //  @Test
  //  public void testGetDiaryIndex() throws Exception {
  //    // Arrange
  //    Long memberId = 1L;
  //    YearMonth yearMonth = YearMonth.of(2024, 10);
  //    DiaryIndexResponse indexResponse = new DiaryIndexResponse(List.of());
  //
  //    when(diaryService.getIndex(memberId, yearMonth)).thenReturn(indexResponse);
  //
  //    // Act and Assert
  //    mockMvc
  //        .perform(
  //            MockMvcRequestBuilders.get("/api/v1/diary/my?yearMonth=2024-10")
  //                .header("X-Todak-User-Id", memberId))
  //        .andExpect(MockMvcResultMatchers.status().isOk())
  //        .andExpect(MockMvcResultMatchers.jsonPath("$.diaryIndexes").isArray());
  //  }
  //
  //  @Test
  //  public void testGetDiary() throws Exception {
  //    // Arrange
  //    Long memberId = 1L;
  //    LocalDate requestDate = LocalDate.of(2024, 10, 26);
  //    DiaryResponse diaryResponse =
  //        DiaryResponse.builder()
  //            .diaryId(1L)
  //            .emotion("emotion")
  //            .content("content")
  //            .webtoonImageUrls(List.of())
  //            .bgmUrl("bgmUrl")
  //            .aiComment("aiComment")
  //            .date(requestDate)
  //            .build();
  //
  //    when(diaryService.getDiary(memberId, requestDate)).thenReturn(diaryResponse);
  //
  //    // Act and Assert
  //    mockMvc
  //        .perform(
  //            MockMvcRequestBuilders.get("/api/v1/diary/my/detail?date=2024-10-26")
  //                .header("X-Todak-User-Id", memberId))
  //        .andExpect(MockMvcResultMatchers.status().isOk())
  //        .andExpect(MockMvcResultMatchers.jsonPath("$.diaryId").value(1L))
  //        .andExpect(MockMvcResultMatchers.jsonPath("$.emotion").value("emotion"))
  //        .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("content"))
  //        .andExpect(MockMvcResultMatchers.jsonPath("$.bgmUrl").value("bgmUrl"))
  //        .andExpect(MockMvcResultMatchers.jsonPath("$.aiComment").value("aiComment"))
  //        .andExpect(MockMvcResultMatchers.jsonPath("$.date").value("2024-10-26"));
  //  }
  //
  //  @Test
  //  public void testWriteDiaryWithDailyWritingLimitExceededException() throws Exception {
  //    // Arrange
  //    Long memberId = 1L;
  //    DiaryWriteRequest request = new DiaryWriteRequest("emotion", "content",
  // LocalDateTime.now());
  //
  //    when(diaryService.write(memberId, request))
  //        .thenThrow(new DiaryDailyWritingLimitExceedException(null, memberId));
  //
  //    // Act and Assert
  //    mockMvc
  //        .perform(
  //            MockMvcRequestBuilders.post("/api/v1/diary/my")
  //                .contentType(MediaType.APPLICATION_JSON)
  //                .content(objectMapper.writeValueAsString(request))
  //                .header("X-Todak-User-Id", memberId))
  //        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  //  }
  //
  //  @Test
  //  public void testDeleteDiaryWithNotFoundException() throws Exception {
  //    // Arrange
  //    Long memberId = 1L;
  //    Long diaryId = 1L;
  //
  //    Mockito.doThrow(new DiaryDeleteNotFoundException(null, memberId, diaryId))
  //        .when(diaryService)
  //        .delete(memberId, diaryId);
  //
  //    // Act and Assert
  //    mockMvc
  //        .perform(
  //            MockMvcRequestBuilders.delete("/api/v1/diary/my/{diaryId}", diaryId)
  //                .header("X-Todak-User-Id", memberId))
  //        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  //  }
  //
  //  @Test
  //  public void testGetDiaryWithNotFoundException() throws Exception {
  //    // Arrange
  //    Long memberId = 1L;
  //    LocalDate requestDate = LocalDate.of(2024, 10, 26);
  //
  //    when(diaryService.getDiary(memberId, requestDate))
  //        .thenThrow(new DiaryNotFoundException(null, memberId, requestDate));
  //
  //    // Act and Assert
  //    mockMvc
  //        .perform(
  //            MockMvcRequestBuilders.get("/api/v1/diary/my/detail?date=2024-10-26")
  //                .header("X-Todak-User-Id", memberId))
  //        .andExpect(MockMvcResultMatchers.status().isNotFound());
  //  }
  //
  //  @Test
  //  public void testGetDiaryWithMemberNotFoundException() throws Exception {
  //    // Arrange
  //    Long memberId = 1L;
  //    LocalDate requestDate = LocalDate.of(2024, 10, 26);
  //
  //    when(diaryService.getDiary(memberId, requestDate))
  //        .thenThrow(new MemberNotFoundException(null, memberId));
  //
  //    // Act and Assert
  //    mockMvc
  //        .perform(
  //            MockMvcRequestBuilders.get("/api/v1/diary/my/detail?date=2024-10-26")
  //                .header("X-Todak-User-Id", memberId))
  //        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  //  }
}
