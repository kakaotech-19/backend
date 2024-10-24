package com.heartsave.todaktodak_api.common;

import com.heartsave.todaktodak_api.common.security.WithCustomMockUserSecurityContextFactory;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory.class)
public @interface WithMockTodakUser {
  String username() default "testUser";

  long id() default 1L;

  String role() default "ROLE_USER";
}
