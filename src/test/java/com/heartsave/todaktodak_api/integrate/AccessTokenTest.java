package com.heartsave.todaktodak_api.integrate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartsave.todaktodak_api.ai.client.service.AiClientService;
import com.heartsave.todaktodak_api.auth.dto.request.LoginRequest;
import com.heartsave.todaktodak_api.common.BaseTestObject;
import com.heartsave.todaktodak_api.diary.repository.DiaryRepository;
import com.heartsave.todaktodak_api.member.entity.MemberEntity;
import com.heartsave.todaktodak_api.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrateTestConfiguration.class)
public class AccessTokenTest {

  @Autowired ObjectMapper objectMapper;
  @Autowired MockMvc mockMvc;

  @Autowired private MemberRepository memberRepository;

  @Autowired private DiaryRepository diaryRepository;

  @Autowired private AiClientService aiClientService;

  private MemberEntity member;

  @BeforeEach
  void setup() {
    member = BaseTestObject.createDBMember();
    assertThat(mockingDetails(memberRepository).isMock()).isTrue();
    System.out.println("memberRepository.hashCode() = " + memberRepository.hashCode());
    System.out.println("diaryRepository.hashCode() = " + diaryRepository.hashCode());
    System.out.println("aiClientService.hashCode() = " + aiClientService.hashCode());
    when(memberRepository.findMemberEntityByLoginId(member.getLoginId()))
        .thenReturn(Optional.of(member));
  }

  @Test
  @DisplayName("로그인시 AccessToken 발급 성공")
  void login_get_accessToken_success() throws Exception {
    LoginRequest request = new LoginRequest(member.getLoginId(), BaseTestObject.TEST_PASSWORD);

    mockMvc
        .perform(post("/api/v1/auth/login").content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.accessToken").isString())
        .andDo(print());
  }

  @Test
  @DisplayName("회원이 없는 loginId로 로그인 시 401 unAuthorized 응답")
  void login_user_not_found() throws Exception {
    LoginRequest request = new LoginRequest("notExist_LoginId", "password");

    mockMvc
        .perform(post("/api/v1/auth/login").content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }
}
