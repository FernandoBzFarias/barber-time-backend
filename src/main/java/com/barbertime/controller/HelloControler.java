package com.barbertime.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloControler {
	
	 @GetMapping("/hello")
	    public String hello() {
	        return "BarberTime API está rodando 🚀";
	    }

}
