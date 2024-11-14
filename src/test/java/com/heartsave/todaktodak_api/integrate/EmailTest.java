package com.heartsave.todaktodak_api.integrate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartsave.todaktodak_api.auth.dto.request.EmailCheckRequest;
import com.heartsave.todaktodak_api.auth.dto.request.EmailOtpCheckRequest;
import com.heartsave.todaktodak_api.member.repository.MemberRepository;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Import(RepositoryConfiguration.class)
@DisplayName("이메일 인증 통합 테스트")
public class EmailTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private JavaMailSender mockMailSender;

  @Qualifier("otpRedisTemplate")
  @Autowired
  private RedisTemplate<String, String> mockRedisTemplate;

  @Mock private ValueOperations<String, String> valueOperations;

  @Autowired private MemberRepository memberRepository;

  @BeforeEach
  void setup() {
    assertThat(mockingDetails(mockMailSender).isMock()).isTrue();
    assertThat(mockingDetails(mockRedisTemplate).isMock()).isTrue();
    when(mockMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
    when(mockRedisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Test
  @DisplayName("이메일 OTP 전송 - OTP를 정상적으로 생성하고 전송한다")
  public void testSendEmailOtp() throws Exception {
    EmailCheckRequest request = new EmailCheckRequest("test@example.com");
    when(memberRepository.existsByEmail(anyString())).thenReturn(false);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(print());

    verify(mockMailSender, times(1)).send(any(MimeMessage.class));
    verify(valueOperations, times(1)).set(anyString(), anyString(), any(Duration.class));
  }

  @Test
  @DisplayName("이메일 OTP 검증 - 올바른 OTP를 검증한다")
  public void testVerifyEmailOtp() throws Exception {
    EmailOtpCheckRequest request = new EmailOtpCheckRequest("test@example.com", "12345678");
    when(valueOperations.get(anyString())).thenReturn("12345678");

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/email/otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(print());

    verify(mockRedisTemplate, times(1)).delete(anyString());
  }

  @Test
  @DisplayName("이메일 OTP 검증 실패 - 잘못된 OTP를 검증한다")
  public void testVerifyEmailOtpFail() throws Exception {
    EmailOtpCheckRequest request = new EmailOtpCheckRequest("test@example.com", "12345678");
    when(mockRedisTemplate.opsForValue().get(anyString())).thenReturn("87654321");

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/email/otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andDo(print());
  }
}
