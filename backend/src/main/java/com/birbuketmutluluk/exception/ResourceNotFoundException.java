package com.birbuketmutluluk.exception;

import org.springframework.http.HttpStatus;

/**
 * Kaynak bulunamadığında 404 döndürür. ApiException alt sınıfı olduğu için
 * GlobalExceptionHandler tarafından otomatik işlenir.
 */
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
