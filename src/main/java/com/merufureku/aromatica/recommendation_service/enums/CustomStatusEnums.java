package com.merufureku.aromatica.recommendation_service.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomStatusEnums {

    NO_USER_FOUND(4000, "No User Found",HttpStatus.NOT_FOUND),
    INVALID_TOKEN(4001, "Invalid Token", HttpStatus.UNAUTHORIZED),
    NO_USER_COLLECTION(4002, "User has no collection yet", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(4003, "Invalid Request Occurred",HttpStatus.BAD_REQUEST);


    private int statusCode;
    private String message;
    private HttpStatus httpStatus;

    CustomStatusEnums(int statusCode, String message, HttpStatus httpStatus) {
        this.statusCode = statusCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
