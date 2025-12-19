package com.merufureku.aromatica.recommendation_service.helper;

import com.merufureku.aromatica.recommendation_service.exceptions.ServiceException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;

import static com.merufureku.aromatica.recommendation_service.enums.CustomStatusEnums.SERVICE_UNAVAILABLE;

public final class AsyncExceptionMapper {

    private AsyncExceptionMapper() {}

    public static RuntimeException map(Throwable ex) {

        Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;

        if (cause instanceof ServiceException se) {
            return se;
        }

        if (cause instanceof CallNotPermittedException) {
            return new ServiceException(SERVICE_UNAVAILABLE);
        }

        if (cause instanceof SocketTimeoutException ||
            cause instanceof ConnectException ||
            cause instanceof ResourceAccessException ||
            cause instanceof HttpServerErrorException ||
            cause instanceof TimeoutException
        ) {
            return new ServiceException(SERVICE_UNAVAILABLE);
        }

        return new RuntimeException(cause);
    }
}
