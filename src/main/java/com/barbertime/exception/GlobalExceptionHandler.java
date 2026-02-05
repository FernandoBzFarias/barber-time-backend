package com.barbertime.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
	 @ExceptionHandler(RuntimeException.class)
	    public ResponseEntity<ApiError> handleRuntime(RuntimeException ex) {
	        ApiError error = new ApiError(
	                HttpStatus.BAD_REQUEST.value(),
	                ex.getMessage());
	        return ResponseEntity.badRequest().body(error);
	    }

	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
	        ApiError error = new ApiError(
	                HttpStatus.INTERNAL_SERVER_ERROR.value(),
	                "Erro interno no servidor");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	   }
	    
	    @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {

	        String errors = ex.getBindingResult()
	                .getFieldErrors()
	                .stream()
	                .map(err -> err.getDefaultMessage())
	                .collect(Collectors.joining(", "));

	        ApiError apiError = new ApiError(400, errors);

	        return ResponseEntity.badRequest().body(apiError);
	    }
}
