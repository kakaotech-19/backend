package com.heartsave.todaktodak_api.member.controller;

import com.heartsave.todaktodak_api.common.security.domain.TodakUser;
import com.heartsave.todaktodak_api.member.dto.request.NicknameUpdateRequest;
import com.heartsave.todaktodak_api.member.dto.response.MemberProfileResponse;
import com.heartsave.todaktodak_api.member.dto.response.NicknameUpdateResponse;
import com.heartsave.todaktodak_api.member.service.MemberService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원", description = "회원 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
  private final MemberService memberService;

  @PatchMapping("/nickname")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "닉네임 변경 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
        @ApiResponse(responseCode = "404", description = "회원 조회 실패")
      })
  public ResponseEntity<NicknameUpdateResponse> updateNickname(
      @AuthenticationPrincipal TodakUser user, @Valid @RequestBody NicknameUpdateRequest dto) {
    return ResponseEntity.ok(memberService.updateNickname(user, dto));
  }

  @GetMapping("/profile")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "회원 프로필 조회"),
        @ApiResponse(responseCode = "404", description = "회원 조회 실패")
      })
  public ResponseEntity<MemberProfileResponse> getMemberProfile(
      @AuthenticationPrincipal TodakUser user) {
    return ResponseEntity.ok(memberService.getMemberProfileById(user));
  }
}