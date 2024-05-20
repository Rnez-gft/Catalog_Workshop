package com.gftworkshopcatalog.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Generated
public class ErrorResponse {
    private String message;
    private HttpStatus status;
}