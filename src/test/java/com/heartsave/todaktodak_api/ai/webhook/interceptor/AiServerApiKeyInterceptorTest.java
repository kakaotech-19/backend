package com.heartsave.todaktodak_api.ai.webhook.interceptor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.heartsave.todaktodak_api.ai.webhook.controller.AiWebhookController;
import com.heartsave.todaktodak_api.ai.webhook.service.AiDiaryService;
import com.heartsave.todaktodak_api.ai.webhook.test_config.TestInterceptorSecurityConfig;
import com.heartsave.todaktodak_api.common.exception.errorspec.AiErrorSpec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({AiWebhookController.class})
@AutoConfigureMockMvc
@ActiveProfiles("test") // Interceptor Bean의 api-key와 테스트의 api-key를 동일하게 설정
@Import(TestInterceptorSecurityConfig.class)
class AiServerApiKeyInterceptorTest {

  @Autowired private MockMvc mockMvc;
  @MockBean AiDiaryService aiService; // Controller Load 위해 필요

  @Value("${ai.server.api.key}")
  private String correctApiKey;

  private String X_API_KEY = "X-API-KEY";
  private String AI_HOOK_WEBTOON_URL = "/api/v1/webhook/ai/webtoon";

  @Test
  @DisplayName("유효한 API KEY 성공")
  void validApiKey_Success() throws Exception {
    mockMvc
        .perform(post(AI_HOOK_WEBTOON_URL).header(X_API_KEY, correctApiKey))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("API KEY 없으면 실패")
  void noApiKey_Fail() throws Exception {
    // when & then
    mockMvc
        .perform(post(AI_HOOK_WEBTOON_URL))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.title").value(AiErrorSpec.INVALID_API_KEY.name()))
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @DisplayName("유효하지 않은 API KEY 실패")
  void invalidApiKey_Fail() throws Exception {
    mockMvc
        .perform(post(AI_HOOK_WEBTOON_URL).header(X_API_KEY, "wrong-key"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.title").value(AiErrorSpec.INVALID_API_KEY.name()))
        .andExpect(jsonPath("$.message").exists());
  }
}