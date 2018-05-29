package io.github.yoshikawaa.app.web;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.yoshikawaa.app.entity.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestClientControllerAdvice {

    @Autowired
    private ObjectMapper mapper;

    @ExceptionHandler(HttpClientErrorException.class)
    public String handleHttpClientErrorException(Model model, HttpClientErrorException e) throws IOException {
        log.warn("Res Code -> {}:{}", e.getStatusCode(), e.getStatusText());
        log.warn("Res Body -> {}", e.getResponseBodyAsString());
        model.addAttribute("status", e.getStatusCode());
        model.addAttribute("error", e.getStatusText());
        model.addAttribute("responseBody", mapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class));
        return "error/422";
    }

}
