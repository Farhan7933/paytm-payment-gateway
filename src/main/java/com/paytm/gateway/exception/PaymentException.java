package com.paytm.gateway.exception;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {
    private String errorCode;

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
