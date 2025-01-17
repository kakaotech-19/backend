package com.heartsave.todaktodak_api.member.entity;

import static com.heartsave.todaktodak_api.common.constant.ConstraintConstant.Member.*;
import static com.heartsave.todaktodak_api.common.constant.CoreConstant.URL.TEMP_CHARACTER_IMAGE_URL_PREFIX;

import com.heartsave.todaktodak_api.common.entity.BaseEntity;
import com.heartsave.todaktodak_api.common.security.domain.AuthType;
import com.heartsave.todaktodak_api.member.domain.CharacterCache;
import com.heartsave.todaktodak_api.member.domain.TodakRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "member")
public class MemberEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member")
  @SequenceGenerator(name = "member", sequenceName = "member_seq", allocationSize = 1)
  private Long id;

  @Column(length = EMAIL_MAX_SIZE)
  private String email;

  @Column(length = NICKNAME_MAX_SIZE)
  private String nickname;

  @Column(length = LOGIN_ID_MAX_SIZE)
  private String loginId;

  @Column(length = PASSWORD_MAX_SIZE)
  private String password;

  private String characterImageUrl;

  // AI 컨텐츠 사전 정보
  @Column(columnDefinition = "text")
  private String characterInfo;

  @Enumerated(EnumType.STRING)
  private AuthType authType;

  @Enumerated(EnumType.STRING)
  private TodakRole role;

  private Integer characterSeed;

  private String characterStyle;

  public void updateNickname(String newNickName) {
    nickname = newNickName;
  }

  public void updateRole(String role) {
    this.role = TodakRole.valueOf(role);
  }

  public void updateCharacterInfo(CharacterCache cache) {
    characterInfo = cache.characterInfo();
    characterStyle = cache.characterStyle();
    characterSeed = cache.characterSeed();
    characterImageUrl = cache.characterImageUrl().replace(TEMP_CHARACTER_IMAGE_URL_PREFIX, "");
  }
}
