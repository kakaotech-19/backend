package com.heartsave.todaktodak_api.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConstraintConstant {

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class Member {
    public static final int EMAIL_MAX_SIZE = 50;
    public static final int NICKNAME_MAX_SIZE = 50;
    public static final int LOGIN_ID_MAX_SIZE = 50;
    public static final int PASSWORD_MAX_SIZE = 80;
  }
}
