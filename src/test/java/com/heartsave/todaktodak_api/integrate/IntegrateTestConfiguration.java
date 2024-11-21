package com.heartsave.todaktodak_api.integrate;

import com.heartsave.todaktodak_api.ai.client.service.AiClientService;
import com.heartsave.todaktodak_api.auth.repository.OtpCacheRepository;
import com.heartsave.todaktodak_api.diary.repository.DiaryReactionRepository;
import com.heartsave.todaktodak_api.diary.repository.DiaryRepository;
import com.heartsave.todaktodak_api.diary.repository.MySharedDiaryRepository;
import com.heartsave.todaktodak_api.diary.repository.PublicDiaryRepository;
import com.heartsave.todaktodak_api.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.mail.javamail.JavaMailSender;

@org.springframework.boot.test.context.TestConfiguration
@Slf4j
public class IntegrateTestConfiguration implements ApplicationListener<ContextRefreshedEvent> {

  @MockBean private MemberRepository memberRepository;
  @MockBean private DiaryRepository diaryRepository;
  @MockBean private AiClientService aiClientService;
  @MockBean private MySharedDiaryRepository mySharedDiaryRepository;
  @MockBean private PublicDiaryRepository publicDiaryRepository;
  @MockBean private DiaryReactionRepository reactionRepository;
  @MockBean private JavaMailSender mailSender;
  @MockBean private OtpCacheRepository mockOtpCacheRepository;
  @Autowired ApplicationContext context;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    StringBuilder sb = new StringBuilder();
    sb.append(
        "context.getBeanDefinitionNames().length = "
            + context.getBeanDefinitionNames().length
            + "\n");
    for (String name : context.getBeanDefinitionNames()) {
      sb.append("name : " + name + "\n");
    }
    log.info("{}", sb);
  }

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
