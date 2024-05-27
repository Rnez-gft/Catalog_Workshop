package com.gftworkshopcatalog.exceptions;

import lombok.Generated;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Generated
@Getter
public class AddCategoryInvalidArgumentsExceptions extends RuntimeException {
    private final HttpStatus status;

    public AddCategoryInvalidArgumentsExceptions(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

}