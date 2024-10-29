package com.heartsave.todaktodak_api.common.security.component.oauth2;

import com.heartsave.todaktodak_api.common.security.domain.TodakUser;
import com.heartsave.todaktodak_api.member.domain.TodakRole;
import com.heartsave.todaktodak_api.member.entity.MemberEntity;
import com.heartsave.todaktodak_api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

// 리소스 -> MemberEntity와 TodakUser로 변환
//
@Service
@RequiredArgsConstructor
public class TodakOauth2UserDetailsService extends DefaultOAuth2UserService {
  private final MemberRepository memberRepository;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
    OAuth2User user = super.loadUser(request);
    TodakOauth2Attribute attribute =
        TodakOauth2Attribute.of(
            user.getAttributes(), request.getClientRegistration().getRegistrationId());
    if (attribute == null) return null;
    logger.info("OAUTH2 RESOURCE - {}", attribute);
    // 회원 저장
    MemberEntity member = memberRepository.save(createMember(attribute));
    // 저장 성공시 인증 정보 생성
    return TodakUser.from(member);
  }

  private MemberEntity createMember(TodakOauth2Attribute attribute) {
    return MemberEntity.builder()
        .authType(attribute.getAuthType())
        .email(attribute.getEmail())
        .role(TodakRole.ROLE_TEMP)
        .nickname(createNickname(attribute.getEmail(), attribute.getAuthType().name()))
        .loginId(attribute.getUsername())
        .build();
  }

  private String createNickname(String email, String authType) {
    return email.split("@")[0] + "_" + authType;
  }
}
