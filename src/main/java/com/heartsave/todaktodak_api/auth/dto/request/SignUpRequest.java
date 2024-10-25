package com.heartsave.todaktodak_api.auth.dto.request;

import static com.heartsave.todaktodak_api.common.security.constant.ConstraintConstant.Member.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청 데이터")
public record SignUpRequest(
    @NotBlank(message = "Blank data") @Size(max = EMAIL_MAX_SIZE, message = "Exceed size")
        String email,
    @NotBlank(message = "Blank data") @Size(max = NICKNAME_MAX_SIZE, message = "Exceed size")
        String nickname,
    @NotBlank(message = "Blank data") @Size(max = LOGIN_ID_MAX_SIZE, message = "Exceed size")
        String loginId,
    @NotBlank(message = "Blank data") @Size(max = PASSWORD_MAX_SIZE, message = "Exceed size")
        String password) {}