package com.loan.origination.system.util.http;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.api.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public HttpErrorInfo handleNotFoundException(ServerHttpRequest request, NotFoundException ex) {
    return createHttpErrorInfo(NOT_FOUND, request, ex);
  }

  @ResponseStatus(UNPROCESSABLE_ENTITY)
  @ExceptionHandler(InvalidInputException.class)
  public HttpErrorInfo handleInvalidInputException(
      ServerHttpRequest request, InvalidInputException ex) {

    return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
  }

  private HttpErrorInfo createHttpErrorInfo(
      HttpStatus httpStatus, ServerHttpRequest request, Exception ex) {
    final String path = request.getPath().pathWithinApplication().value();
    final String message = ex.getMessage();
    return new HttpErrorInfo(path, httpStatus, message);
  }
}
