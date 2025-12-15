package com.merufureku.aromatica.recommendation_service.services;

import com.merufureku.aromatica.recommendation_service.config.UrlConfig;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.CollectionsResponse;
import com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums;
import com.merufureku.aromatica.recommendation_service.exceptions.ServiceException;
import com.merufureku.aromatica.recommendation_service.helper.RestExceptionHelper;
import com.merufureku.aromatica.recommendation_service.services.impl.CollectionsService;
import com.merufureku.aromatica.recommendation_service.utilities.TokenUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectionsServiceTest {

    @InjectMocks
    private CollectionsService collectionsService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UrlConfig urlConfig;

    @Mock
    private TokenUtility tokenUtility;

    @Mock
    private RestExceptionHelper restExceptionHelper;

    private static final Integer USER_ID = 1;
    private static final int VERSION = 1;
    private static final String CORRELATION_ID = "corr-1";

    @Test
    void getUserCollections_whenSuccessful_shouldReturnResponse() {
        var collectionsResponse = new CollectionsResponse(USER_ID, Collections.emptyList());
        var baseResponse = new BaseResponse<>(HttpStatus.OK.value(), "success", collectionsResponse);

        when(urlConfig.getCollectionUrl()).thenReturn("http://collection-service");
        when(tokenUtility.generateInternalToken(anyString())).thenReturn("token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(baseResponse, HttpStatus.OK));

        BaseResponse<CollectionsResponse> response = collectionsService.getUserCollections(USER_ID, VERSION, CORRELATION_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertNotNull(response.data());
    }

    @Test
    void getUserCollections_whenBodyOrDataNull_shouldThrowServiceException() {
        when(urlConfig.getCollectionUrl()).thenReturn("http://collection-service");
        when(tokenUtility.generateInternalToken(anyString())).thenReturn("token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> collectionsService.getUserCollections(USER_ID, VERSION, CORRELATION_ID));

        assertEquals(CustomStatusEnums.NO_USER_COLLECTION, exception.getCustomStatusEnums());
    }

    @Test
    void getUserCollections_whenHttpClientErrorException_shouldThrowServiceException() {
        HttpClientErrorException httpException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "bad request");

        when(urlConfig.getCollectionUrl()).thenReturn("http://collection-service");
        when(tokenUtility.generateInternalToken(anyString())).thenReturn("token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenThrow(httpException);
        doThrow(new ServiceException(CustomStatusEnums.INVALID_REQUEST)).when(restExceptionHelper).handleException(any(HttpClientErrorException.class));

        assertThrows(ServiceException.class,
                () -> collectionsService.getUserCollections(USER_ID, VERSION, CORRELATION_ID));
    }
}
