package com.gftworkshopcatalog.exceptions;

import lombok.Generated;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Generated
@Getter
public class InternalServiceException extends RuntimeException {
    private final HttpStatus status;

    public InternalServiceException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}