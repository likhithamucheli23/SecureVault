package com.securevault.dto;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private UserDTO user;
    private boolean mfaRequired = false;
    private String mfaToken;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, String tokenType, UserDTO user, boolean mfaRequired, String mfaToken, String message) {
        this.token = token;
        if (tokenType != null) {
            this.tokenType = tokenType;
        }
        this.user = user;
        this.mfaRequired = mfaRequired;
        this.mfaToken = mfaToken;
        this.message = message;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String token;
        private String tokenType = "Bearer";
        private UserDTO user;
        private boolean mfaRequired = false;
        private String mfaToken;
        private String message;

        public AuthResponseBuilder token(String token) { this.token = token; return this; }
        public AuthResponseBuilder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public AuthResponseBuilder user(UserDTO user) { this.user = user; return this; }
        public AuthResponseBuilder mfaRequired(boolean mfaRequired) { this.mfaRequired = mfaRequired; return this; }
        public AuthResponseBuilder mfaToken(String mfaToken) { this.mfaToken = mfaToken; return this; }
        public AuthResponseBuilder message(String message) { this.message = message; return this; }

        public AuthResponse build() {
            return new AuthResponse(token, tokenType, user, mfaRequired, mfaToken, message);
        }
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
    public boolean isMfaRequired() { return mfaRequired; }
    public void setMfaRequired(boolean mfaRequired) { this.mfaRequired = mfaRequired; }
    public String getMfaToken() { return mfaToken; }
    public void setMfaToken(String mfaToken) { this.mfaToken = mfaToken; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
