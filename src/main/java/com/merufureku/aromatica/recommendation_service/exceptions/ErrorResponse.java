package com.merufureku.aromatica.recommendation_service.exceptions;

public record ErrorResponse(int status, String error, String message,
                            String path, String timestamp) {}
