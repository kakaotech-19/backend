package com.heartsave.todaktodak_api.common.security.component;

import com.heartsave.todaktodak_api.common.security.domain.TodakUser;
import com.heartsave.todaktodak_api.member.entity.MemberEntity;
import com.heartsave.todaktodak_api.member.repository.MemberRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TodakUserDetailsService implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    MemberEntity memberEntity =
        memberRepository
            .findMemberEntityByLoginId(username)
            .orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND"));
    return TodakUser.builder()
        .id(memberEntity.getId())
        .username(memberEntity.getLoginId())
        .password(memberEntity.getPassword())
        .role(memberEntity.getRole().name())
        .attributes(Map.of())
        .build();
  }
}
