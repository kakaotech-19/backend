package com.heartsave.todaktodak_api.integrate;

import static com.heartsave.todaktodak_api.common.BaseTestObject.DUMMY_STRING_CONTENT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartsave.todaktodak_api.diary.constant.DiaryEmotion;
import com.heartsave.todaktodak_api.diary.dto.request.DiaryWriteRequest;
import com.heartsave.todaktodak_api.listener.ContextLoadTimeTestExecutionListener;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrateTestConfiguration.class)
@TestExecutionListeners(
    value = ContextLoadTimeTestExecutionListener.class,
    mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public class AuthorizationTest {
  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @Test
  public void anonyToDiaryWrite_Fail() throws Exception {
    DiaryWriteRequest request =
        new DiaryWriteRequest(LocalDateTime.now(), DiaryEmotion.HAPPY, DUMMY_STRING_CONTENT);
    mockMvc
        .perform(post("/api/v1/diary/my").content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }
}
