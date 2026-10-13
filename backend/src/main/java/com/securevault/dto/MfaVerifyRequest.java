package com.securevault.dto;

import jakarta.validation.constraints.NotBlank;

public class MfaVerifyRequest {

    @NotBlank
    private String mfaToken;

    @NotBlank
    private String code;

    public MfaVerifyRequest() {}

    public MfaVerifyRequest(String mfaToken, String code) {
        this.mfaToken = mfaToken;
        this.code = code;
    }

    public String getMfaToken() { return mfaToken; }
    public void setMfaToken(String mfaToken) { this.mfaToken = mfaToken; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
