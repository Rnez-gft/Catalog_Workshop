package com.gftworkshopcatalog.exceptions;

import lombok.Generated;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Generated
@Getter
public class BadRequest extends RuntimeException {
    private final HttpStatus status;

    public BadRequest(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
