package io.github.yoshikawaa.app.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestClientControllerAdvice {

    @ExceptionHandler(HttpClientErrorException.class)
    public void handleHttpClientErrorException(HttpClientErrorException e) {
        log.warn("Res Code -> {}:{}", e.getStatusCode(), e.getStatusText());
        log.warn("Res Body -> {}", e.getResponseBodyAsString());
        throw e;
    }

}
