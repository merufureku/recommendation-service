package com.merufureku.aromatica.recommendation_service.services.impl;

import com.merufureku.aromatica.recommendation_service.config.UrlConfig;
import com.merufureku.aromatica.recommendation_service.dto.params.ExcludeFragranceBatchNotesParam;
import com.merufureku.aromatica.recommendation_service.dto.params.FragranceBatchNotesParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceNoteListResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceResponse;
import com.merufureku.aromatica.recommendation_service.helper.RestExceptionHelper;
import com.merufureku.aromatica.recommendation_service.services.interfaces.IFragranceService;
import com.merufureku.aromatica.recommendation_service.utilities.TokenUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static com.merufureku.aromatica.recommendation_service.constants.RecommendationCollectionConstants.FRAGRANCE_SERVICE;

@Service
public class FragranceService implements IFragranceService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final RestTemplate restTemplate;
    private final UrlConfig urlConfig;
    private final TokenUtility tokenUtility;
    private final RestExceptionHelper restExceptionHelper;

    public FragranceService(RestTemplate restTemplate, UrlConfig urlConfig, TokenUtility tokenUtility, RestExceptionHelper restExceptionHelper) {
        this.restTemplate = restTemplate;
        this.urlConfig = urlConfig;
        this.tokenUtility = tokenUtility;
        this.restExceptionHelper = restExceptionHelper;
    }

    public BaseResponse<FragranceNoteListResponse> getPerfumeNotes(FragranceBatchNotesParam param, int version, String correlationId) {
        try{
            var url = new StringBuilder();
            url
                    .append(urlConfig.getFragranceUrl())
                    .append("/internal/fragrances/batch/notes")
                    .append("?version=")
                    .append(version)
                    .append("&correlationId=")
                    .append(correlationId);

            logger.info("Fetching perfume notes from URL: {}", url.toString());

            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(getToken());

            ResponseEntity<BaseResponse<FragranceNoteListResponse>> responseEntity = restTemplate.exchange(
                    url.toString(), HttpMethod.POST, new HttpEntity<>(param, headers), new ParameterizedTypeReference<>() {}
            );

            logger.info("Received perfume notes response with status code: {}", responseEntity.getStatusCode());
            return responseEntity.getBody();
        }
        catch (HttpClientErrorException ex){
            throw restExceptionHelper.handleException(ex);
        }
    }

    @Override
    public BaseResponse<FragranceNoteListResponse> getPerfumeNotes(ExcludeFragranceBatchNotesParam param, int version, String correlationId) {
        try {
            var url = new StringBuilder();
            url
                    .append(urlConfig.getFragranceUrl())
                    .append("/internal/fragrances/exclude/notes")
                    .append("?version=")
                    .append(version)
                    .append("&correlationId=")
                    .append(correlationId);

            logger.info("Fetching All perfume and notes from URL: {}", url.toString());

            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(getToken());

            ResponseEntity<BaseResponse<FragranceNoteListResponse>> responseEntity = restTemplate.exchange(
                    url.toString(), HttpMethod.POST, new HttpEntity<>(param, headers), new ParameterizedTypeReference<>() {}
            );

            logger.info("Received All perfume and notes response with status code: {}", responseEntity.getStatusCode());
            return responseEntity.getBody();
        }
        catch (HttpClientErrorException ex){
            throw restExceptionHelper.handleException(ex);
        }
    }

    @Override
    public BaseResponse<FragranceResponse> getPerfumeById(Long fragranceId, int version, String correlationId) {
        try{
            var url = new StringBuilder();
            url
                    .append(urlConfig.getFragranceUrl())
                    .append("/public/fragrances/")
                    .append(fragranceId)
                    .append("?version=")
                    .append(version)
                    .append("&correlationId=")
                    .append(correlationId);

            logger.info("Fetching perfume by ID from URL: {}", url.toString());

            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<BaseResponse<FragranceResponse>> responseEntity = restTemplate.exchange(
                    url.toString(), HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {}
            );

            logger.info("Received perfume by ID response with status code: {}", responseEntity.getStatusCode());
            return responseEntity.getBody();
        }
        catch (HttpClientErrorException ex){
            throw restExceptionHelper.handleException(ex);
        }
    }

    private String getToken(){
        return tokenUtility.generateInternalToken(FRAGRANCE_SERVICE);
    }

}

