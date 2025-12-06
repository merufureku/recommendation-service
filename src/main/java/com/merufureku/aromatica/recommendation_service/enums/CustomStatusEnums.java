package com.merufureku.aromatica.recommendation_service.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomStatusEnums {

    NO_USER_FOUND(4000, "No User Found",HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(4001, "Invalid Token", HttpStatus.UNAUTHORIZED),
    FRAGRANCE_NOT_FOUND(4002, "Perfume not found", HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_EXISTS(4003, "Review already exist", HttpStatus.CONFLICT),
    REVIEW_NOT_FOUND(4004, "Review not found", HttpStatus.NOT_FOUND);


    private int statusCode;
    private String message;
    private HttpStatus httpStatus;

    CustomStatusEnums(int statusCode, String message, HttpStatus httpStatus) {
        this.statusCode = statusCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
