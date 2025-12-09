package com.merufureku.aromatica.recommendation_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeyConfig {

    @Value("${jwt.access.secret.key}")
    private String jwtAccessSecretKey;

    @Value("${jwt.internal.fragrance.secret.key}")
    private String jwtInternalFragranceSecretKey;

    @Value("${jwt.internal.collection.secret.key}")
    private String jwtInternalCollectionSecretKey;

    public String getJwtAccessSecretKey() {
        return jwtAccessSecretKey;
    }

    public String getJwtInternalFragranceSecretKey() { return jwtInternalFragranceSecretKey; }

    public String getJwtInternalCollectionSecretKey() { return jwtInternalCollectionSecretKey; }
}
