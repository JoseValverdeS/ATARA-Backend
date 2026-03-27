package com.atara.deb.ataraapi.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class LogoutRequestDto {

    @NotBlank(message = "El refresh token es obligatorio")
    private String refreshToken;

    public LogoutRequestDto() {}

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
