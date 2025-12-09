package com.merufureku.aromatica.recommendation_service.utilities;

import com.merufureku.aromatica.recommendation_service.config.KeyConfig;
import com.merufureku.aromatica.recommendation_service.exceptions.ServiceException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

import static com.merufureku.aromatica.recommendation_service.constants.RecommendationCollectionConstants.COLLECTION_SERVICE;
import static com.merufureku.aromatica.recommendation_service.constants.RecommendationCollectionConstants.FRAGRANCE_SERVICE;
import static com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums.INVALID_TOKEN;

@Component
public class TokenUtility {

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_SERVICE = "service";
    private static final String INTERNAL = "internal";

    private final KeyConfig keyConfig;

    public TokenUtility(KeyConfig keyConfig) {
        this.keyConfig = keyConfig;
    }

    public String generateInternalToken(String serviceName) {

        var secretKeyString = getSecretKey(serviceName);

        var keyBytes = Base64.getDecoder().decode(secretKeyString);
        var secretKey = Keys.hmacShaKeyFor(keyBytes);

        long expirationMillis = 24L * 60 * 60 * 1000; // 24 hours

        var token = Jwts.builder()
                .claim(CLAIM_TYPE, INTERNAL)
                .claim(CLAIM_SERVICE, serviceName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    public Claims parseToken(String token) {

        try{
            var secretKey = Keys.hmacShaKeyFor(Base64.getDecoder()
                    .decode(keyConfig.getJwtAccessSecretKey()));

            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (Exception e){
            throw new ServiceException(INVALID_TOKEN);
        }
    }

    private String getSecretKey(String service) {
        return switch(service){
            case FRAGRANCE_SERVICE  -> keyConfig.getJwtInternalFragranceSecretKey();
            case COLLECTION_SERVICE -> keyConfig.getJwtInternalCollectionSecretKey();
            default -> throw new ServiceException(INVALID_TOKEN);
        };
    }
}
