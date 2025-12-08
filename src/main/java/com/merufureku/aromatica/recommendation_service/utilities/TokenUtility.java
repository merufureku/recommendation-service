package com.merufureku.aromatica.recommendation_service.utilities;

import com.merufureku.aromatica.recommendation_service.config.KeyConfig;
import com.merufureku.aromatica.recommendation_service.exceptions.ServiceException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Base64;

import static com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums.INVALID_TOKEN;

@Component
public class TokenUtility {

    private final KeyConfig keyConfig;

    public TokenUtility(KeyConfig keyConfig) {
        this.keyConfig = keyConfig;
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
}
