package com.cursorclash.backend.RegistrationAndLogin.response;

public class LoginResponse {
    private String  message;
    private Boolean status;
    private String token;

    public LoginResponse(String message, Boolean status, String token) {
        this.message = message;
        this.status = status;
        this.token = token;
    }

    public LoginResponse(String message, Boolean status) {
        this.message = message;
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public LoginResponse(){}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
