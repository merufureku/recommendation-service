package com.merufureku.aromatica.recommendation_service.utilities;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.merufureku.aromatica.recommendation_service.constants.RecommendationCollectionConstants.ACCESS_TOKEN_EXPIRATION_MINUTES;

@Component
public class DateUtility {

    public static boolean isAccessTokenExpired(LocalDateTime expirationTime) {
        var now = LocalDateTime.now();

        expirationTime.minusMinutes(ACCESS_TOKEN_EXPIRATION_MINUTES);

        return now.isAfter(expirationTime);
    }
}
