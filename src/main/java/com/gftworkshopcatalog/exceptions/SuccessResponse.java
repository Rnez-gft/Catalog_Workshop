package com.gftworkshopcatalog.exceptions;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Generated
public class SuccessResponse {
    private String message;

    public SuccessResponse(String message) {
        this.message = message;
    }

}
