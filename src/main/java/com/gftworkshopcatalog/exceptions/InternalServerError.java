package com.gftworkshopcatalog.exceptions;

import lombok.Generated;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Generated
@Getter
public class InternalServerError extends RuntimeException {
    private final HttpStatus status;

    public InternalServerError(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public InternalServerError(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}