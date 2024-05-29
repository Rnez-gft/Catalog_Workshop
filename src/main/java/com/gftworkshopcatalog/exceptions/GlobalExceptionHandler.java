package com.gftworkshopcatalog.exceptions;

import lombok.Generated;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Generated
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequest exception) {
        ErrorResponse response = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(exception.getStatus())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AddProductInvalidArgumentsExceptions.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleAddProductInvalidArgumentsExceptions(AddProductInvalidArgumentsExceptions exception) {
        ErrorResponse response = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(exception.getStatus())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AddCategoryInvalidArgumentsExceptions.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleAddCategoryInvalidArgumentsExceptions(AddCategoryInvalidArgumentsExceptions exception) {
        ErrorResponse response = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(exception.getStatus())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AddPromotionInvalidArgumentsExceptions.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleAddPromotionInvalidArgumentsExceptions(AddPromotionInvalidArgumentsExceptions exception) {
        ErrorResponse response = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(exception.getStatus())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NotFoundProduct.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundProduct exception) {
        ErrorResponse response = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(NotFoundCategory.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFoundCategory(NotFoundCategory exception) {
        ErrorResponse response = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(exception.getStatus())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(NotFoundPromotion.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFoundPromotion(NotFoundPromotion exception) {
        ErrorResponse response = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(exception.getStatus())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DatabaseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DatabaseException exception) {
        ErrorResponse response = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(exception.getStatus())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(InternalServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleInternalServiceException(InternalServiceException exception) {
        ErrorResponse response = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(exception.getStatus())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(InternalServerError ex) {
        ErrorResponse errorResponse = new ErrorResponse( ex.getMessage(),ex.getStatus());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = "Invalid parameter: " + ex.getName() + ". value '" + ex.getValue() +
                "' cannot be converted ";
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(errorMessage)
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .message("Error accessing data from database")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
