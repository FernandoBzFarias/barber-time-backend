package com.barbertime.entity;
import java.time.LocalDateTime;

import jakarta.persistence.*;
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String token;

	    private String email;

	    private LocalDateTime expiration;

	    public PasswordResetToken() {}

	    public PasswordResetToken(String token, String email, LocalDateTime expiration) {
	        this.token = token;
	        this.email = email;
	        this.expiration = expiration;
	    }

	    public String getToken() { return token; }
	    public String getEmail() { return email; }
	    public LocalDateTime getExpiration() { return expiration; }
}
