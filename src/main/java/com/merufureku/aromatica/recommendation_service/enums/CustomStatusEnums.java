package com.merufureku.aromatica.recommendation_service.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomStatusEnums {

    NO_USER_FOUND(4000, "No User Found",HttpStatus.NOT_FOUND),
    INVALID_TOKEN(4001, "Invalid Token", HttpStatus.UNAUTHORIZED),
    NO_USER_COLLECTION(4002, "User has no collection yet", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(4003, "Invalid Request Occurred",HttpStatus.BAD_REQUEST),
    RECOMMENDATION_FAILED(4004, "Recommendation Service Failed",HttpStatus.BAD_REQUEST),
    NO_FRAGRANCE_TO_RECOMMEND(4005, "No Fragrance to Recommend",HttpStatus.BAD_REQUEST),
    NOTE_NOT_EXIST(4042, "Note ID not found", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(5000, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(5003, "Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE);

    private int statusCode;
    private String message;
    private HttpStatus httpStatus;

    CustomStatusEnums(int statusCode, String message, HttpStatus httpStatus) {
        this.statusCode = statusCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
