package com.kiettuan.identity_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    USER_EXISTED(1001, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1002, "User not existed", HttpStatus.NOT_FOUND),
    INVALID_USERNAME(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1005, "Invalid message key" , HttpStatus.BAD_REQUEST), // 404
    UNAUTHENTICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED), // 401
    UNAUTHORIZED(1008, "You do not have permissions", HttpStatus.FORBIDDEN), // 403
    INVALID_DOB(1009,"Your age must be at least {min}", HttpStatus.BAD_REQUEST ),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR) // Code: 500

    ;

    ErrorCode( int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
