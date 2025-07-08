package com.loan.origination.system.api.exceptions;

public class NotFoundException extends RuntimeException {

  public NotFoundException() {}

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public NotFoundException(Throwable cause) {
    super(cause);
  }
}
