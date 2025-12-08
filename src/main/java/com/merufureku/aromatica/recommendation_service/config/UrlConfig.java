package com.merufureku.aromatica.recommendation_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlConfig {

    @Value("${url.collection}")
    private String collectionUrl;

    @Value("${url.fragrance}")
    private String fragranceUrl;

    public String getCollectionUrl() { return collectionUrl; }

    public String getFragranceUrl() { return fragranceUrl; }
}
