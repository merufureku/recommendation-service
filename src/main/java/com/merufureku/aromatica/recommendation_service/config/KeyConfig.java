package com.merufureku.aromatica.recommendation_service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class KeyConfig {

    @Value("${jwt.access.secret.key}")
    private String jwtAccessSecretKey;

    @Value("${jwt.internal.fragrance.secret.key}")
    private String jwtInternalFragranceSecretKey;

    @Value("${jwt.internal.collection.secret.key}")
    private String jwtInternalCollectionSecretKey;

    @Value("${jwt.internal.review.secret.key}")
    private String jwtInternalReviewSecretKey;

}
