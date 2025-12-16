package com.merufureku.aromatica.recommendation_service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class UrlConfig {

    @Value("${url.collection}")
    private String collectionUrl;

    @Value("${url.fragrance}")
    private String fragranceUrl;

    @Value("${url.review}")
    private String reviewUrl;
}
