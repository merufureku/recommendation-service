package com.merufureku.aromatica.recommendation_service.services;

import com.merufureku.aromatica.recommendation_service.config.UrlConfig;
import com.merufureku.aromatica.recommendation_service.dto.params.ExcludeFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.recommendation_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceDetailedListResponse;
import com.merufureku.aromatica.recommendation_service.dto.responses.FragranceNoteListResponse;
import com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums;
import com.merufureku.aromatica.recommendation_service.exceptions.ServiceException;
import com.merufureku.aromatica.recommendation_service.helper.RestExceptionHelper;
import com.merufureku.aromatica.recommendation_service.services.impl.FragranceService;
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
class FragranceServiceTest {

    @InjectMocks
    private FragranceService fragranceService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UrlConfig urlConfig;

    @Mock
    private TokenUtility tokenUtility;

    @Mock
    private RestExceptionHelper restExceptionHelper;

    private static final int VERSION = 1;
    private static final String CORRELATION_ID = "corr-1";

    @Test
    void getPerfumes_whenSuccessful_shouldReturnResponse() {
        var param = new GetFragranceBatchParam(Collections.singleton(1L));
        var detailedResponse = new FragranceDetailedListResponse(Collections.emptyList());
        var baseResponse = new BaseResponse<>(HttpStatus.OK.value(), "success", detailedResponse);

        when(urlConfig.getFragranceUrl()).thenReturn("http://fragrance-service");
        when(tokenUtility.generateInternalToken(anyString())).thenReturn("token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(baseResponse, HttpStatus.OK));

        BaseResponse<FragranceDetailedListResponse> response = fragranceService.getPerfumes(param, VERSION, CORRELATION_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertNotNull(response.data());
    }

    @Test
    void getPerfumes_whenHttpClientErrorException_shouldThrowServiceException() {
        var param = new GetFragranceBatchParam(Collections.singleton(1L));
        HttpClientErrorException httpException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "bad request");

        when(urlConfig.getFragranceUrl()).thenReturn("http://fragrance-service");
        when(tokenUtility.generateInternalToken(anyString())).thenReturn("token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenThrow(httpException);
        doThrow(new ServiceException(CustomStatusEnums.INVALID_REQUEST))
                .when(restExceptionHelper).handleException(any(HttpClientErrorException.class));

        assertThrows(ServiceException.class,
                () -> fragranceService.getPerfumes(param, VERSION, CORRELATION_ID));
    }

    @Test
    void getPerfumeNotes_withBatchParam_whenSuccessful_shouldReturnResponse() {
        var param = new GetFragranceBatchParam(Collections.singleton(1L));
        var noteListResponse = new FragranceNoteListResponse(Collections.emptyList());
        var baseResponse = new BaseResponse<>(HttpStatus.OK.value(), "success", noteListResponse);

        when(urlConfig.getFragranceUrl()).thenReturn("http://fragrance-service");
        when(tokenUtility.generateInternalToken(anyString())).thenReturn("token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(baseResponse, HttpStatus.OK));

        BaseResponse<FragranceNoteListResponse> response = fragranceService.getPerfumeNotes(param, VERSION, CORRELATION_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertNotNull(response.data());
    }

    @Test
    void getPerfumeNotes_withBatchParam_whenBodyOrDataNull_shouldThrowServiceException() {
        var param = new GetFragranceBatchParam(Collections.singleton(1L));

        when(urlConfig.getFragranceUrl()).thenReturn("http://fragrance-service");
        when(tokenUtility.generateInternalToken(anyString())).thenReturn("token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> fragranceService.getPerfumeNotes(param, VERSION, CORRELATION_ID));

        assertEquals(CustomStatusEnums.NOTE_NOT_EXIST, exception.getCustomStatusEnums());
    }

    @Test
    void getPerfumeNotes_withBatchParam_whenHttpClientErrorException_shouldThrowServiceException() {
        var param = new GetFragranceBatchParam(Collections.singleton(1L));
        HttpClientErrorException httpException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "bad request");

        when(urlConfig.getFragranceUrl()).thenReturn("http://fragrance-service");
        when(tokenUtility.generateInternalToken(anyString())).thenReturn("token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenThrow(httpException);
        doThrow(new ServiceException(CustomStatusEnums.INVALID_REQUEST))
                .when(restExceptionHelper).handleException(any(HttpClientErrorException.class));

        assertThrows(ServiceException.class,
                () -> fragranceService.getPerfumeNotes(param, VERSION, CORRELATION_ID));
    }

    @Test
    void getPerfumeNotes_withExcludeParam_whenSuccessful_shouldReturnResponse() {
        var param = new ExcludeFragranceBatchParam(Collections.singleton(1L));
        var noteListResponse = new FragranceNoteListResponse(Collections.emptyList());
        var baseResponse = new BaseResponse<>(HttpStatus.OK.value(), "success", noteListResponse);

        when(urlConfig.getFragranceUrl()).thenReturn("http://fragrance-service");
        when(tokenUtility.generateInternalToken(anyString())).thenReturn("token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(baseResponse, HttpStatus.OK));

        BaseResponse<FragranceNoteListResponse> response = fragranceService.getPerfumeNotes(param, VERSION, CORRELATION_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertNotNull(response.data());
    }

    @Test
    void getPerfumeNotes_withExcludeParam_whenBodyOrDataNull_shouldThrowServiceException() {
        var param = new ExcludeFragranceBatchParam(Collections.singleton(1L));

        when(urlConfig.getFragranceUrl()).thenReturn("http://fragrance-service");
        when(tokenUtility.generateInternalToken(anyString())).thenReturn("token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> fragranceService.getPerfumeNotes(param, VERSION, CORRELATION_ID));

        assertEquals(CustomStatusEnums.NOTE_NOT_EXIST, exception.getCustomStatusEnums());
    }

    @Test
    void getPerfumeNotes_withExcludeParam_whenHttpClientErrorException_shouldThrowServiceException() {
        var param = new ExcludeFragranceBatchParam(Collections.singleton(1L));
        HttpClientErrorException httpException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "bad request");

        when(urlConfig.getFragranceUrl()).thenReturn("http://fragrance-service");
        when(tokenUtility.generateInternalToken(anyString())).thenReturn("token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenThrow(httpException);
        doThrow(new ServiceException(CustomStatusEnums.INVALID_REQUEST))
                .when(restExceptionHelper).handleException(any(HttpClientErrorException.class));

        assertThrows(ServiceException.class,
                () -> fragranceService.getPerfumeNotes(param, VERSION, CORRELATION_ID));
    }
}
