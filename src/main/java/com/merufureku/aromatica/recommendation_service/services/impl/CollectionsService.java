package com.merufureku.aromatica.recommendation_service.services.impl;

import com.merufureku.aromatica.recommendation_service.config.UrlConfig;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.CollectionsResponse;
import com.merufureku.aromatica.recommendation_service.services.interfaces.ICollectionService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CollectionsService implements ICollectionService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final RestTemplate restTemplate;
    private final HttpServletRequest request;
    private final UrlConfig urlConfig;

    public CollectionsService(RestTemplate restTemplate, HttpServletRequest request, UrlConfig urlConfig) {
        this.restTemplate = restTemplate;
        this.request = request;
        this.urlConfig = urlConfig;
    }

    public BaseResponse<CollectionsResponse> getUserCollections(int version, String correlationId) {

        var url = new StringBuilder();
        url
                .append(urlConfig.getCollectionUrl())
                .append("/collections")
                .append("?version=")
                .append(version)
                .append("&correlationId=")
                .append(correlationId);

        logger.info("Fetching User Collections from URL: {}", url.toString());

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAccessToken());

        ResponseEntity<BaseResponse<CollectionsResponse>> responseEntity = restTemplate.exchange(
                url.toString(), HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {}
        );

        return responseEntity.getBody();
    }

    private String getAccessToken(){
        return request.getHeader("Authorization").substring(7);
    }
}

