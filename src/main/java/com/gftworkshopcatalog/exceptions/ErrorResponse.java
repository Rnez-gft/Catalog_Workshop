package com.gftworkshopcatalog.exceptions;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Generated
public class ErrorResponse {
    String message;
    int errorCode;
}
//Quitar de la pipeline
