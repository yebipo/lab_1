package com.antiprocrastinate.lab.exception;

public class BusinessOperationException extends RuntimeException {
  public BusinessOperationException(String message) {
    super(message);
  }
}