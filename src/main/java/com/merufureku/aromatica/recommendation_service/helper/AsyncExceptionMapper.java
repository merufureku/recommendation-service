package com.merufureku.aromatica.recommendation_service.helper;

import com.merufureku.aromatica.recommendation_service.exceptions.ServiceException;

import java.util.concurrent.CompletionException;

import static com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums.RECOMMENDATION_FAILED;

public final class AsyncExceptionMapper {

    private AsyncExceptionMapper() {}

    public static RuntimeException map(Throwable ex) {

        Throwable cause = ex instanceof CompletionException
                ? ex.getCause()
                : ex;

        if (cause instanceof ServiceException se) {
            return se;
        }

        return new ServiceException(RECOMMENDATION_FAILED);
    }
}
