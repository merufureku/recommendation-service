package com.merufureku.aromatica.recommendation_service.helper;

import com.merufureku.aromatica.recommendation_service.exceptions.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import static com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums.*;

@Component
public class RestExceptionHelper {

    private final Logger logger = LogManager.getLogger(this.getClass());

    public HttpClientErrorException handleException(HttpClientErrorException ex) {

        logger.error("HTTP Client Error: {}", ex.getMessage());

        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new ServiceException(NO_USER_FOUND);
        }
        else if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED){
            throw new ServiceException(INVALID_TOKEN);
        }
        else {
            throw new ServiceException(INVALID_REQUEST);
        }
    }

}
