package com.najung.todo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @Schema(description = "회원ID", example = "najung5")
        @NotBlank(message = "아이디를 입력해주세요")
        String userId,
        @Schema(description = "회원PW", example = "1q2w3e4r!!")
        @NotBlank(message = "비밀번호를 입력해주세요")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=]).{8,20}$",
                message = "비밀번호는 8~20자이며, 영문/숫자/특수문자를 포함해야 합니다."
        )
        String userPassword
) {
    public static LoginRequest of(String userId, String userPassword) {
        return new LoginRequest(userId, userPassword);
    }
}
