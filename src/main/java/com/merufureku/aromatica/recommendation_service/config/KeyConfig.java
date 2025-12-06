package com.merufureku.aromatica.recommendation_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeyConfig {

    @Value("${jwt.access.secret.key}")
    private String jwtAccessSecretKey;

    public String getJwtAccessSecretKey() {
        return jwtAccessSecretKey;
    }
}
