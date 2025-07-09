package com.loan.origination.system.util.http;

import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;

public class HttpErrorInfo {
  private final ZonedDateTime timestamp;
  private final String path;
  private final HttpStatus httpStatus;
  private final String message;

  public HttpErrorInfo() {
    this.timestamp = null;
    this.path = null;
    this.httpStatus = null;
    this.message = null;
  }

  public HttpErrorInfo(String path, HttpStatus httpStatus, String message) {
    this.timestamp = ZonedDateTime.now();
    this.path = path;
    this.httpStatus = httpStatus;
    this.message = message;
  }

  public ZonedDateTime getTimestamp() {
    return timestamp;
  }

  public String getPath() {
    return path;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public String getMessage() {
    return message;
  }
}
