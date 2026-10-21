package com.birbuketmutluluk.exception;

import org.springframework.http.HttpStatus;

/**
 * İş kuralı kaynaklı, HTTP durumu taşıyan uygulama istisnası.
 * GlobalExceptionHandler bunu standart ErrorResponse'a çevirir.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
