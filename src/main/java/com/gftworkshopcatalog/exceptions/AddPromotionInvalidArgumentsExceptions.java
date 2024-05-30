package com.gftworkshopcatalog.exceptions;

import lombok.Generated;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Generated
@Getter
public class AddPromotionInvalidArgumentsExceptions extends RuntimeException {
    private final HttpStatus status;

    public AddPromotionInvalidArgumentsExceptions(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

}