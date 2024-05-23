package com.gftworkshopcatalog.exceptions;

import lombok.Generated;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Generated
@Getter
public class NotFoundProduct extends RuntimeException {
    private final HttpStatus status;

    public NotFoundProduct(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }
}