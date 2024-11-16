package com.heartsave.todaktodak_api.integrate;

import com.heartsave.todaktodak_api.ai.client.service.AiClientService;
import com.heartsave.todaktodak_api.auth.repository.OtpCacheRepository;
import com.heartsave.todaktodak_api.diary.repository.DiaryReactionRepository;
import com.heartsave.todaktodak_api.diary.repository.DiaryRepository;
import com.heartsave.todaktodak_api.diary.repository.MySharedDiaryRepository;
import com.heartsave.todaktodak_api.diary.repository.PublicDiaryRepository;
import com.heartsave.todaktodak_api.member.repository.MemberRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

@org.springframework.boot.test.context.TestConfiguration(proxyBeanMethods = false)
public class IntegrateTestConfiguration {

  @MockBean private MemberRepository memberRepository;
  @MockBean private DiaryRepository diaryRepository;
  @MockBean private AiClientService aiClientService;
  @MockBean private MySharedDiaryRepository mySharedDiaryRepository;
  @MockBean private PublicDiaryRepository publicDiaryRepository;
  @MockBean private DiaryReactionRepository reactionRepository;
  @MockBean private JavaMailSender mailSender;
  @MockBean private OtpCacheRepository mockOtpCacheRepository;

  //  @Bean
  //  @Primary
  //  public JavaMailSender mockJavaMailSender() {
  //    return mock(JavaMailSender.class);
  //  }
  //
  //  @Bean
  //  @Primary
  //  public OtpCacheRepository mockOtpCacheRepository() {
  //    return mock(OtpCacheRepository.class);
  //  }
  //
  //  @Bean
  //  @Primary
  //  public DiaryReactionRepository mockDiaryReactionRepository() {
  //    return mock(DiaryReactionRepository.class);
  //  }
  //
  //  @Bean
  //  @Primary
  //  public MySharedDiaryRepository mockMySharedDiaryRepository() {
  //    return mock(MySharedDiaryRepository.class);
  //  }
  //
  //  @Bean
  //  @Primary
  //  public PublicDiaryRepository mockPublicDiaryRepository() {
  //    return mock(PublicDiaryRepository.class);
  //  }
  //
  //  @Bean
  //  @Primary
  //  public MemberRepository mockMemberRepository() {
  //    return mock(MemberRepository.class);
  //  }
  //
  //  @Bean
  //  @Primary
  //  public DiaryRepository mockDiaryRepository() {
  //    return mock(DiaryRepository.class);
  //  }
  //
  //  @Bean
  //  @Primary
  //  public AiClientService mockAiClientService() {
  //    return mock(AiClientService.class);
  //  }
}