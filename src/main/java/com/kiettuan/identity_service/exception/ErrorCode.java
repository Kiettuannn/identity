package com.kiettuan.identity_service.exception;

public enum ErrorCode {
    USER_EXISTED(1001, "User existed"),
    USER_NOT_FOUND(1002, "User not found"),
    INVALID_USERNAME(1003, "Username must be at least 3 characters"),
    INVALID_PASSWORD(1004, "Password must be at least 8 characters"),
    INVALID_KEY(1005, "Invalid message key"),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized")

    ;

    ErrorCode( int code, String message) {
        this.code = code;
        this.message = message;

    }
    private int code;
    private String message;


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
