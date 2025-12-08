package com.merufureku.aromatica.recommendation_service.dto.responses;

public record BaseResponse<T>(int status, String message, T data){}
