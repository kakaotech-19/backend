package com.heartsave.todaktodak_api.common.security.component.oauth2;

import static com.heartsave.todaktodak_api.common.security.constant.ConstraintConstant.Member.LOGIN_ID_MAX_SIZE;

import com.heartsave.todaktodak_api.common.security.domain.AuthType;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 소셜 로그인별로 정형화된 형식의 리소스로 가공
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TodakOauth2Attribute {
  private static final String NAVER = "naver";
  private static final String KAKAO = "kakao";
  private static final String GOOGLE = "google";

  private final String username;
  private final String email;
  private final AuthType authType;

  //
  public static TodakOauth2Attribute of(Map<String, Object> attributes, String authType) {
    return switch (authType) {
      case NAVER -> ofNaver(attributes);
      case KAKAO -> ofKakao(attributes);
      case GOOGLE -> ofGoogle(attributes);
      default -> null;
    };
  }

  @SuppressWarnings("unchecked")
  private static TodakOauth2Attribute ofKakao(Map<String, Object> attributes) {
    String id = String.valueOf(attributes.get("id")); // Long -> String 변환
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    String email = (String) kakaoAccount.get("email");
    return new TodakOauth2Attribute(id + "_" + KAKAO, email, AuthType.KAKAO);
  }

  @SuppressWarnings("unchecked")
  private static TodakOauth2Attribute ofNaver(Map<String, Object> attributes) {
    Map<String, Object> response = (Map<String, Object>) attributes.get("response");
    String id = (String) response.get("id");
    id = id.substring(0, Math.min(id.length(), LOGIN_ID_MAX_SIZE - 6));
    String email = (String) response.get("email");
    return new TodakOauth2Attribute(id + "_" + NAVER, email, AuthType.NAVER);
  }

  private static TodakOauth2Attribute ofGoogle(Map<String, Object> attributes) {
    return new TodakOauth2Attribute(
        attributes.get("sub") + "_" + GOOGLE, (String) attributes.get("email"), AuthType.GOOGLE);
  }

  @Override
  public String toString() {
    return """
USERNAME: %s, EMAIL: %s, AUTH TYPE: %s"""
        .formatted(username, email, authType.name());
  }
}
