package com.algawors.algacomments.comment.service.api.config.web;

import com.algawors.algacomments.comment.service.api.exception.ModerationClientBadGatewayException;
import com.algawors.algacomments.comment.service.api.exception.ModerationClientUnprocessableEntityException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.channels.ClosedChannelException;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ENTITY_NOT_FOUND = "Entity Not Found!";

    @ExceptionHandler({
            SocketTimeoutException.class,
            ConnectException.class,
            ClosedChannelException.class
    })
    public ProblemDetail handle(IOException exception) {
        return createProblemDetail(HttpStatus.GATEWAY_TIMEOUT, "Gateway Timeout", exception.getMessage(), "/errors/gateway-timeout") ;
    }

    @ExceptionHandler(ModerationClientBadGatewayException.class)
    public ProblemDetail handle(ModerationClientBadGatewayException exception) {
        return createProblemDetail(HttpStatus.BAD_GATEWAY, "Bad Gateway", exception.getMessage(), "/errors/bad-gateway") ;
    }

    @ExceptionHandler(ModerationClientUnprocessableEntityException.class)
    public ProblemDetail handle(ModerationClientUnprocessableEntityException exception) {
        return createProblemDetail(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", exception.getMessage(), "/errors/unprocessable-entity") ;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handle(EntityNotFoundException exception) {
        return createProblemDetail(HttpStatus.NOT_FOUND, "Entity Not Found", ENTITY_NOT_FOUND, "/errors/entity-not-found") ;
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail, String url) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);

        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);
        problemDetail.setType(URI.create(url));

        return problemDetail;
    }

}
