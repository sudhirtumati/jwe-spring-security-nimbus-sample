package com.sample.security.resourceserver.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GreetingControllerSecurityTests {

	@Autowired
	private MockMvc mockMvc;

	private static ClientAndServer mockServer;

	private static String authenticationToken;

	@BeforeAll
	static void initialize() throws URISyntaxException, IOException {
		authenticationToken = Files.readString(
				Paths.get(GreetingControllerSecurityTests.class.getClassLoader().getResource("jwe.txt").toURI()));
		mockServer = ClientAndServer.startClientAndServer(32323);
		// Mock response for jwks.json
		String jwksJsonContent = Files.readString(
				Paths.get(GreetingControllerSecurityTests.class.getClassLoader().getResource("sign.jwks").toURI()));
		mockServer.when(HttpRequest.request().withMethod("GET").withPath("/.well-known/jwks.json"))
				.respond(HttpResponse.response().withStatusCode(200).withBody(jwksJsonContent));
		// Mock response for jwks.json
		String accessEncryptJwksContent = Files.readString(
				Paths.get(GreetingControllerSecurityTests.class.getClassLoader().getResource("encrypt.jwks").toURI()));
		mockServer.when(HttpRequest.request().withMethod("GET").withPath("/authn/keys/access-encrypt"))
				.respond(HttpResponse.response().withStatusCode(200).withBody(accessEncryptJwksContent));
	}

	@AfterAll
	static void cleanup() {
		mockServer.stop();
	}

	@Test
	void given_authorization_token_header_greet_should_respond_with_200() throws Exception {
		mockMvc.perform(get("/greet/John").header("Authorization", "Bearer " + authenticationToken))
				.andExpect(status().isOk());
	}

	@Test
	void when_no_authorization_token_header_given_greet_should_respond_with_401() throws Exception {
		mockMvc.perform(get("/greet/John")).andExpect(status().isUnauthorized());
	}

}
