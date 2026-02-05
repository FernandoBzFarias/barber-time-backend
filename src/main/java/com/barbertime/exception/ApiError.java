package com.barbertime.exception;

import java.time.LocalDateTime;

public class ApiError {
	  private LocalDateTime timestamp;
	  private int status;
	  private String error;

	   public ApiError(int status, String error) {
	      this.timestamp = LocalDateTime.now();
	      this.status = status;
	      this.error = error;
	    }

	    public LocalDateTime getTimestamp() {
	       return timestamp;
	    }

	    public int getStatus() {
	       return status;
	    }

	    public String getError() {
	       return error;
	    }
}
