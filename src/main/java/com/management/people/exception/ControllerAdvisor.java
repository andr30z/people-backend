package com.management.people.exception;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.management.people.dto.ApiError;

import lombok.extern.slf4j.Slf4j;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<Object> handleEntityNotFoundException(
                        ResourceNotFoundException exception) {
                return buildResponseEntity(
                                HttpStatus.NOT_FOUND,
                                exception.getMessage(),
                                Collections.singletonList(exception.getMessage()));
        }

        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex, HttpHeaders headers,
                        HttpStatusCode status, WebRequest request) {

                List<String> errors = ex.getBindingResult().getFieldErrors()
                                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());

                return buildResponseEntity(HttpStatus.BAD_REQUEST,
                                "Malformed data", errors);
        }

        protected ResponseEntity<Object> handleHttpMessageNotReadable(
                        HttpMessageNotReadableException exception, HttpHeaders headers, HttpStatusCode status,
                        WebRequest request) {

                return buildResponseEntity(
                                HttpStatus.BAD_REQUEST,
                                exception.getMessage(),
                                Collections.singletonList(exception.getMessage()));
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<Object> handleBadRequestException(
                        BadRequestException exception) {
                log.info(exception.getMessage());
                return buildResponseEntity(
                                HttpStatus.BAD_REQUEST,
                                exception.getMessage(),
                                Collections.singletonList(exception.getMessage()));
        }

        public ResponseEntity<Object> buildResponseEntity(
                        HttpStatus httpStatus,
                        String message,
                        List<String> errors) {
                ApiError apiError = ApiError
                                .builder()
                                .code(httpStatus.value())
                                .status(httpStatus.getReasonPhrase())
                                .message(message)
                                .errors(errors)
                                .timestamp(LocalDateTime.now())
                                .build();
                return ResponseEntity.status(httpStatus).body(apiError);

        }
}
