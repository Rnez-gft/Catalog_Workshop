package com.gftworkshopcatalog.exceptions;

import lombok.Generated;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Generated
@Getter
public class AddProductInvalidArgumentsExceptions extends RuntimeException {
    private final HttpStatus status;

    public AddProductInvalidArgumentsExceptions(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

}