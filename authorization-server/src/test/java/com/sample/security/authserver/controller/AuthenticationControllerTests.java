package com.sample.security.authserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void given_invalid_credentials_post_should_throw_401_error() throws Exception {
		mockMvc.perform(post("/authn/validate-credentials").queryParam("userId", "john").queryParam("password", "john"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void given_valid_credentials_post_should_generate_access_token() throws Exception {
		var content = mockMvc
				.perform(post("/authn/validate-credentials").queryParam("userId", "john").queryParam("password", "doe"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertThat(content).isNotEmpty();
	}

}
