package com.gftworkshopcatalog.exceptions;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Generated
public class ErrorResponse {
    private String message;
    private int errorCode;
}