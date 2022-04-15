package com.sample.security.resourceserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
public class GreetingController {

	@GetMapping("/greet/{name}")
	@RolesAllowed("ROLE_EMPLOYEE")
	public String greet(@PathVariable String name) {
		return "Hello " + name;
	}

}
