package com.gimnasio.dto;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long accessTokenExpiresIn;
    private long refreshTokenExpiresIn;
    private UserResponse usuario;
    private UserResponse me;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, long accessTokenExpiresIn, long refreshTokenExpiresIn, UserResponse usuario) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
        this.usuario = usuario;
        this.me = usuario;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getAccessTokenExpiresIn() { return accessTokenExpiresIn; }
    public void setAccessTokenExpiresIn(long accessTokenExpiresIn) { this.accessTokenExpiresIn = accessTokenExpiresIn; }

    public long getRefreshTokenExpiresIn() { return refreshTokenExpiresIn; }
    public void setRefreshTokenExpiresIn(long refreshTokenExpiresIn) { this.refreshTokenExpiresIn = refreshTokenExpiresIn; }

    public UserResponse getUsuario() { return usuario; }
    public void setUsuario(UserResponse usuario) {
        this.usuario = usuario;
        this.me = usuario;
    }

    public UserResponse getMe() { return me; }
    public void setMe(UserResponse me) {
        this.me = me;
        this.usuario = me;
    }
}
