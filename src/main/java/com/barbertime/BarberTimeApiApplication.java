package com.barbertime;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class BarberTimeApiApplication {
	
	@PostConstruct
    void started() {    
        TimeZone.setDefault(TimeZone.getTimeZone("America/Fortaleza"));
    }

    public static void main(String[] args) {
        SpringApplication.run(BarberTimeApiApplication.class, args);
    }
}